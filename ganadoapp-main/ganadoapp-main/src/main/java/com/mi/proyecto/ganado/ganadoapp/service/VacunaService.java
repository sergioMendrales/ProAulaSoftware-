package com.mi.proyecto.ganado.ganadoapp.service;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import com.mi.proyecto.ganado.ganadoapp.model.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.repository.GanadoRepository;
import com.mi.proyecto.ganado.ganadoapp.repository.VacunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VacunaService {

    @Autowired
    private VacunaRepository vacunaRepository;

    @Autowired
    private GanadoRepository ganadoRepository;



    public List<Vacuna> listarTodas() {
        return vacunaRepository.findAll();
    }



    public List<Vacuna> obtenerVacunasPorGanadoId(String ganadoId) {
        return vacunaRepository.findByGanadoId(ganadoId);
    }



    public Vacuna registrarVacuna(Vacuna vacuna) {


        Ganado ganado = ganadoRepository.findById(vacuna.getGanadoId())
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado."));


        // Validaciones de aptitud (edad/peso) deshabilitadas por petición del usuario.
        // Se registra la vacuna sin bloqueo desde el servidor.


        vacuna.setProximaAplicacion(Vacuna.calcularProximaAplicacion(
                vacuna.getNombre(), vacuna.getFechaAplicacion()));

        // Guardamos también el código oficial del ganado en la vacuna para compatibilidad
        vacuna.setCodigoGanado(ganado.getCodigoOficial());

        Vacuna vacunaGuardada = vacunaRepository.save(vacuna);

        ganado.agregarVacunaAlCarnet(vacunaGuardada);
        ganadoRepository.save(ganado);

        return vacunaGuardada;
    }

    public List<Vacuna> obtenerVacunasPorCodigoGanado(String codigoGanado) {
        return vacunaRepository.findByCodigoGanado(codigoGanado);
    }



    public boolean esAptoParaVacuna(Ganado ganado, String nombreVacuna) {
        // Se elimina la validación por edad solicitada; la aptitud se gestionará
        // mediante reglas específicas de peso en validarAptitudVacuna.
        return true;
    }

    /**
     * Valida la aptitud del ganado para una vacuna y devuelve null si es apto.
     * Si no es apto, devuelve una razón legible (edad/peso) para mostrar al usuario.
     */
    public String validarAptitudVacuna(Ganado ganado, String nombreVacuna) {
        // Eliminamos la validación basada en la edad del animal. Solo se usan reglas de peso u otras específicas.
        Double peso = ganado.getPesoKg();

        String n = (nombreVacuna == null) ? "" : nombreVacuna.toLowerCase().trim();

        // Reglas de peso (implementadas con valores conservadores basados en guías veterinarias generales).
        // NOTA: Estas reglas son recomendaciones y pueden variar por región/vacuna/denominación del producto.
        // Ajusta los valores según normativa local o especificación del fabricante.
        if (n.equals("leptospirosis")) {
            // Recomendación: mínimo 30 kg para una respuesta vacunal adecuada en terneros
            if (peso == null || peso < 30.0) {
                return "Peso mínimo para " + nombreVacuna + " es 30 kg.";
            }
        }

        if (n.equals("carbunco") || n.equals("carbunco sintomatico") || n.equals("anthrax")) {
            // Para vacuna contra carbunco se recomienda administrar a animales más robustos; uso de peso mínimo 50 kg
            if (peso == null || peso < 50.0) {
                return "Peso mínimo para " + nombreVacuna + " es 50 kg.";
            }
        }

        return null; // apto
    }


    public void eliminarVacuna(String id) {
        vacunaRepository.deleteById(id);
    }

    public java.util.Optional<Vacuna> obtenerPorId(String id) {
        return vacunaRepository.findById(id);
    }

    public Vacuna actualizarVacuna(Vacuna vacuna) {
        Vacuna existente = vacunaRepository.findById(vacuna.getId())
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada."));

        // Actualizar campos
        existente.setNombre(vacuna.getNombre());
        existente.setFechaAplicacion(vacuna.getFechaAplicacion());
        existente.setIntervaloCantidad(vacuna.getIntervaloCantidad());
        existente.setUnidadTiempo(vacuna.getUnidadTiempo());
        existente.setProximaDosis(vacuna.getProximaDosis());
        existente.setAplicadoPorUsuarioId(vacuna.getAplicadoPorUsuarioId());

        return vacunaRepository.save(existente);
    }
}
