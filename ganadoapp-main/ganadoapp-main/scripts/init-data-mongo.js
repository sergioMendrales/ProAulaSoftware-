// init-data-mongo.js
// Script para mongosh que inserta 100 veterinarios, 500 ganaderos y 20,000 ganado
// Uso: mongosh "mongodb://<user>:<pass>@host:27017/ganadodb?authSource=admin" ./init-data-mongo.js

const usuariosColl = db.getCollection('usuarios');
const ganadoColl = db.getCollection('ganado');

print('Generando veterinarios (100)...');
const veterinarios = [];
for (let i = 1; i <= 100; i++) {
    veterinarios.push({
        nombre: `Veterinario ${i}`,
        email: `vet${i}@example.com`,
        password: `Password123`, // contraseña en texto plano (elegiste A2)
        rol: 'VETERINARIO',
        marcaRegistro: null,
        licencia: `LIC-VET-${String(i).padStart(4,'0')}`
    });
}
const resVets = usuariosColl.insertMany(veterinarios);
print(`Veterinarios insertados: ${Object.keys(resVets.insertedIds).length}`);

print('Generando ganaderos (500)...');
const ganaderos = [];
for (let i = 1; i <= 500; i++) {
    ganaderos.push({
        nombre: `Ganadero ${i}`,
        email: `ganadero${i}@example.com`,
        password: `Password123`, // contraseña en texto plano
        rol: 'GANADERO',
        marcaRegistro: `FARM${String(i).padStart(3,'0')}`,
        licencia: `LIC-GAN-${String(i).padStart(5,'0')}`
    });
}
const resGan = usuariosColl.insertMany(ganaderos);
const ganaderoIds = Object.values(resGan.insertedIds);
print(`Ganaderos insertados: ${ganaderoIds.length}`);

// Distribuir 20,000 ganados entre los 500 ganaderos (40 por ganadero)
const TOTAL_GANADO = 20000;
const perOwner = Math.floor(TOTAL_GANADO / ganaderoIds.length); // 40
const remainder = TOTAL_GANADO - (perOwner * ganaderoIds.length); // 0 expected

print(`Generando ${TOTAL_GANADO} ganados (~${perOwner} por propietario)...`);

const colores = ['Blanco','Negro','Marrón','Atigrado','Gris'];
const razas = ['Holstein','Hereford','Angus','Brahman','Criollo'];
const sexos = ['Macho','Hembra'];
const estados = ['Saludable','Enfermo','Recuperando'];

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function fechaFromEdadMeses(edadMeses) {
    const d = new Date();
    d.setMonth(d.getMonth() - edadMeses);
    // Ajuste simple para fecha ISO (sin hora)
    return d.toISOString().split('T')[0];
}

let totalInserted = 0;
const batchSize = 1000; // insertar en batches para evitar memoria excesiva
let batch = [];

for (let idx = 0; idx < ganaderoIds.length; idx++) {
    const ownerId = ganaderoIds[idx];
    const marca = `FARM${String(idx+1).padStart(3,'0')}`;
    let consecutivo = 1;
    const numForThisOwner = perOwner + (idx < remainder ? 1 : 0);

    for (let j = 0; j < numForThisOwner; j++) {
        const edadMeses = randomInt(1, 120);
        const fechaNacimiento = fechaFromEdadMeses(edadMeses);
        const d = new Date(fechaNacimiento);
        const mes = String(d.getMonth() + 1).padStart(2, '0');
        const anio = String(d.getFullYear() % 100).padStart(2, '0');
        const codigoPadre = String(randomInt(10, 99));
        const codigoOficial = `${marca}-${codigoPadre}-${mes}-${anio}-${String(consecutivo).padStart(3,'0')}`;
        consecutivo++;

        const ganadoDoc = {
            codigoOficial: codigoOficial,
            color: colores[randomInt(0, colores.length - 1)],
            raza: razas[randomInt(0, razas.length - 1)],
            edadMeses: edadMeses,
            pesoKg: Math.round((randomInt(200, 700) + Math.random()) * 10) / 10,
            sexo: sexos[randomInt(0, sexos.length - 1)],
            estadoSalud: estados[randomInt(0, estados.length - 1)],
            propietarioId: ownerId,
            carnetVacunas: [],
            fechaNacimiento: fechaNacimiento
        };

        batch.push(ganadoDoc);
        if (batch.length >= batchSize) {
            const res = ganadoColl.insertMany(batch);
            totalInserted += Object.keys(res.insertedIds).length;
            print(`Inserted batch. Total inserted so far: ${totalInserted}`);
            batch = [];
        }
    }
}

if (batch.length > 0) {
    const res = ganadoColl.insertMany(batch);
    totalInserted += Object.keys(res.insertedIds).length;
    print(`Inserted final batch. Total inserted: ${totalInserted}`);
}

print('Seed completed.');
print(`Veterinarios: ${Object.keys(resVets.insertedIds).length}`);
print(`Ganaderos: ${ganaderoIds.length}`);
print(`Ganado insertado: ${totalInserted}`);
