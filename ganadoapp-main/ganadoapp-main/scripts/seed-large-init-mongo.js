/*
  Seed script for MongoDB (mongosh-compatible)

  Purpose:
  - Insert 50 veterinarios into collection `veterinarios`
  - Insert 100 ganaderos into collection `ganaderos`
  - Insert 50,000 ganado into collection `ganado` (batched)
  - Insert vacunas for a subset of ganado into collection `vacunas` and add a compact record to each ganado's `carnetVacunas` array

  How to run (example):
  mongosh "mongodb://localhost:27017/ganadoapp" scripts/seed-large-init-mongo.js

  Notes:
  - Script uses batching to avoid memory spikes.
  - Adjust DB name or batch sizes if needed.
*/

// If running with `mongosh <script>` the global `db` is available and points to the selected DB.
// Optionally, explicitly select DB by name below (uncomment and edit if you prefer):
// const dbName = 'ganadoapp';
// db = db.getSiblingDB(dbName);

print('Starting seed script...');

// Helpers
function randInt(min, max) { // inclusive
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function pick(arr) {
  return arr[randInt(0, arr.length - 1)];
}

function randomDateMonthsAgo(monthsAgo) {
  const d = new Date();
  d.setMonth(d.getMonth() - monthsAgo);
  // randomize day within that month
  d.setDate(randInt(1, 28));
  d.setHours(0,0,0,0);
  return d;
}

function addMonths(date, months) {
  const d = new Date(date);
  d.setMonth(d.getMonth() + months);
  return d;
}

function addYears(date, years) {
  const d = new Date(date);
  d.setFullYear(d.getFullYear() + years);
  return d;
}

function calcularProximaAplicacion(nombre, fecha) {
  if (!fecha) return null;
  const n = (nombre || '').toLowerCase().trim();
  switch (n) {
    case 'fiebre aftosa':
    case 'fiebreaftosa':
      return addMonths(fecha, 6);
    case 'brucelosis':
      return addYears(fecha, 1);
    case 'carbunco':
    case 'carbunco sintomatico':
      return addYears(fecha, 1);
    case 'rabia':
    case 'rabia bovina':
      return addYears(fecha, 1);
    case 'leptospirosis':
      return addMonths(fecha, 6);
    default:
      return addYears(fecha, 1);
  }
}

// Sample data sets
const nombresVet = [
  'Dr. Alvarez','Dr. Ramirez','Dr. Fernandez','Dr. Garcia','Dr. Torres','Dr. Morales','Dr. Soto','Dr. Herrera','Dr. Navarro','Dr. Castro',
  'Dr. Rojas','Dr. Vega','Dr. Mendoza','Dr. Ortega','Dr. Cruz','Dr. Paredes','Dr. Gutierrez','Dr. Salazar','Dr. Ibarra','Dr. Cardenas',
  'Dr. Leon','Dr. Espinoza','Dr. Fuentes','Dr. Valdez','Dr. Duarte','Dr. Santana','Dr. Delgado','Dr. Solis','Dr. Cardenal','Dr. Montoya',
  'Dr. Ponce','Dr. Aguirre','Dr. Campos','Dr. Prado','Dr. Beltran','Dr. Romo','Dr. Ochoa','Dr. Lira','Dr. Nunez','Dr. Arevalo',
  'Dr. Palacios','Dr. Zamora','Dr. Linares','Dr. Renteria','Dr. Barrera','Dr. Sosa','Dr. Trejo','Dr. Mendoza2','Dr. Quinones','Dr. Bravo'
];

const nombresGanadero = [];
for (let i = 1; i <= 100; i++) {
  nombresGanadero.push('Ganadero ' + String(i).padStart(3, '0'));
}

const colores = ['Blanco','Negro','Marrón','Atigrado','Bayo','Gris'];
const razas = ['Holstein','Hereford','Angus','Brahman','Girolando','Criollo'];
const sexos = ['Macho','Hembra'];
const estados = ['Sano','Enfermo','Desconocido'];

const vacunasCatalogo = [
  'Fiebre Aftosa',
  'Brucelosis',
  'Carbunco',
  'Rabia',
  'Leptospirosis'
];

// Clear or create indices (non-destructive if already exist)
print('Creating indexes...');
try {
  db.usuarios.createIndex({ email: 1 }, { unique: true, background: true });
} catch (e) { print('usuarios index: ' + e); }
try {
  db.ganado.createIndex({ codigoOficial: 1 }, { unique: false, background: true });
} catch (e) { print('ganado index: ' + e); }
try {
  db.vacunas.createIndex({ ganadoId: 1 }, { background: true });
} catch (e) { print('vacunas index: ' + e); }

// 1) Insert veterinarios (50)
print('Inserting veterinarios...');
const veterinarios = [];
for (let i = 0; i < 50; i++) {
  const doc = {
    nombre: nombresVet[i % nombresVet.length] + ' ' + (i+1),
    email: 'vet' + (i+1) + '@example.com',
    password: 'password',
    rol: 'VETERINARIO',
    marcaRegistro: 'M' + randInt(100,999),
    licencia: 'LIC-' + String(randInt(100000,999999))
  };
  veterinarios.push(doc);
}
const resVet = db.veterinarios.insertMany(veterinarios, { ordered: false });
print('Veterinarios inserted: ' + (resVet.insertedCount || veterinarios.length));
const vetIds = Object.values(resVet.insertedIds).map(id => id);

// 2) Insert ganaderos (100)
print('Inserting ganaderos...');
const ganaderos = [];
for (let i = 0; i < 100; i++) {
  const doc = {
    nombre: nombresGanadero[i],
    email: 'ganadero' + (i+1) + '@example.com',
    password: 'password',
    rol: 'GANADERO',
    ganadoIds: []
  };
  ganaderos.push(doc);
}
const resGan = db.ganaderos.insertMany(ganaderos, { ordered: false });
print('Ganaderos inserted: ' + (resGan.insertedCount || ganaderos.length));
const ganaderoIds = Object.values(resGan.insertedIds).map(id => id);

// 3) Insert ganado (50,000) in batches
print('Inserting ganado in batches...');
const totalGanado = 50000;
const batchSize = 1000; // adjust if needed for memory/performance
let ganadoBatch = [];
let ganadorCounter = 0;
let codigoCounter = 1;

for (let i = 0; i < totalGanado; i++) {
  const edadMeses = randInt(1, 120);
  const fechaNacimiento = randomDateMonthsAgo(edadMeses);
  const pesoKg = +(randInt(150, 700) / 10).toFixed(1); // 15.0 - 70.0? adjust scale. Using 15.0-70.0 assumed - but using 15-70 in kg; but we used 150-700/10
  const propietario = pick(ganaderoIds);
  const codigoOficial = 'G-' + String(codigoCounter++).padStart(6, '0');

  const ganadoDoc = {
    codigoOficial: codigoOficial,
    color: pick(colores),
    raza: pick(razas),
    edadMeses: edadMeses,
    pesoKg: pesoKg,
    sexo: pick(sexos),
    estadoSalud: pick(estados),
    propietarioId: propietario,
    carnetVacunas: [],
    fechaNacimiento: fechaNacimiento
  };

  ganadoBatch.push(ganadoDoc);

  if (ganadoBatch.length >= batchSize) {
    const res = db.ganado.insertMany(ganadoBatch, { ordered: false });
    ganadorCounter += (res.insertedCount || ganadoBatch.length);
    if ((ganadorCounter % 5000) === 0) print('Inserted ganado so far: ' + ganadorCounter);
    ganadoBatch = [];
  }
}
// final batch
if (ganadoBatch.length > 0) {
  const res = db.ganado.insertMany(ganadoBatch, { ordered: false });
  ganadorCounter += (res.insertedCount || ganadoBatch.length);
}
print('Total ganado inserted: ' + ganadorCounter);

// 4) Create vacunas for a subset of ganado. We'll iterate over ganado cursor in batches and create vacunas.
print('Inserting vacunas and updating ganado.carnetVacunas...');
const vacunaBatchSize = 1000;
let vacunasBatch = [];
let updatedCount = 0;
let vacunasCreated = 0;

const cursor = db.ganado.find({}, { _id: 1, codigoOficial: 1, fechaNacimiento: 1 });
while (cursor.hasNext()) {
  const g = cursor.next();
  // decide whether to add vaccines to this ganado (30% chance)
  if (Math.random() < 0.30) {
    const vacunasCount = randInt(1, 3);
    const carnetArray = [];
    for (let v = 0; v < vacunasCount; v++) {
      const nombre = pick(vacunasCatalogo);
      const fechaAplicacion = randomDateMonthsAgo(randInt(0, 36)); // last 3 years
      const proxima = calcularProximaAplicacion(nombre, fechaAplicacion);
      const appliedBy = pick(vetIds);
      const vacunaId = new ObjectId();
      const vacunaDoc = {
        _id: vacunaId,
        nombre: nombre,
        descripcion: nombre + ' - aplicado como dato de prueba',
        fechaAplicacion: fechaAplicacion,
        proximaAplicacion: proxima,
        observaciones: '',
        ganadoId: g._id,
        codigoGanado: g.codigoOficial,
        aplicadoPorUsuarioId: appliedBy
      };
      vacunasBatch.push(vacunaDoc);
      vacunasCreated++;

      // compact record to embed in ganado.carnetVacunas
      carnetArray.push({
        id: vacunaId.toString(),
        nombre: nombre,
        fechaAplicacion: fechaAplicacion,
        proximaAplicacion: proxima
      });
    }

    // push the compact carnet entries into ganado document
    db.ganado.updateOne({ _id: g._id }, { $push: { carnetVacunas: { $each: carnetArray } } });
    updatedCount++;
  }

  if (vacunasBatch.length >= vacunaBatchSize) {
    db.vacunas.insertMany(vacunasBatch, { ordered: false });
    vacunasBatch = [];
  }
}
// final vacunas batch
if (vacunasBatch.length > 0) {
  db.vacunas.insertMany(vacunasBatch, { ordered: false });
}
print('Total ganado updated with vaccines: ' + updatedCount);
print('Total vacunas created (approx): ' + vacunasCreated);

print('Seed script finished.');
print('Collections sizes:');
print('veterinarios: ' + db.veterinarios.countDocuments());
print('ganaderos: ' + db.ganaderos.countDocuments());
print('ganado: ' + db.ganado.countDocuments());
print('vacunas: ' + db.vacunas.countDocuments());
