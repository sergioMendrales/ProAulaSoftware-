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


        String razonNoApto = validarAptitudVacuna(ganado, vacuna.getNombre());
        if (razonNoApto != null) {
            String obs = vacuna.getObservaciones();
            String prefijo = "⚠ Recomendación: ";
            if (obs == null || obs.trim().isEmpty()) {
                vacuna.setObservaciones(prefijo + razonNoApto);
            } else {
                vacuna.setObservaciones(obs + " | " + prefijo + razonNoApto);
            }
        }

        vacuna.setProximaAplicacion(Vacuna.calcularProximaAplicacion(
                vacuna.getNombre(), vacuna.getFechaAplicacion()));


        vacuna.setCodigoGanado(ganado.getCodigoOficial());

        Vacuna vacunaGuardada = vacunaRepository.save(vacuna);


        ganado.agregarVacunaAlCarnet(vacunaGuardada);
        ganadoRepository.save(ganado);

        return vacunaGuardada;
    }

    public List<Vacuna> obtenerVacunasPorCodigoGanado(String codigoGanado) {
        return vacunaRepository.findByCodigoGanado(codigoGanado);
    }


    public String validarAptitudVacuna(Ganado ganado, String nombreVacuna) {
        if (ganado == null || nombreVacuna == null) return null;

        Double peso = ganado.getPesoKg();
        Integer edadMeses = ganado.getEdadMeses();
        String n = nombreVacuna.toLowerCase().trim();


        switch (n) {
            case "fiebre aftosa":
            case "fiebreaftosa":
                if (edadMeses != null && edadMeses < 3)
                    return "El ganado debe tener al menos 3 meses para aplicar " + nombreVacuna + ".";
                if (peso != null && peso < 60.0)
                    return "Peso mínimo para aplicar " + nombreVacuna + " es 60 kg.";
                break;

            case "brucelosis":
                if (edadMeses != null && edadMeses < 3)
                    return "El ganado debe tener al menos 3 meses para aplicar " + nombreVacuna + ".";
                break;

            case "carbunco":
            case "carbunco sintomatico":
                if (edadMeses != null && edadMeses < 4)
                    return "El ganado debe tener al menos 4 meses para aplicar " + nombreVacuna + ".";
                if (peso != null && peso < 50.0)
                    return "Peso mínimo para aplicar " + nombreVacuna + " es 50 kg.";
                break;

            case "rabia":
            case "rabia bovina":
                if (edadMeses != null && edadMeses < 3)
                    return "El ganado debe tener al menos 3 meses para aplicar " + nombreVacuna + ".";
                break;

            case "leptospirosis":
                if (edadMeses != null && edadMeses < 2)
                    return "El ganado debe tener al menos 2 meses para aplicar " + nombreVacuna + ".";
                if (peso != null && peso < 30.0)
                    return "Peso mínimo para aplicar " + nombreVacuna + " es 30 kg.";
                break;
        }

        return null;
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

        existente.setNombre(vacuna.getNombre());
        existente.setFechaAplicacion(vacuna.getFechaAplicacion());
        existente.setIntervaloCantidad(vacuna.getIntervaloCantidad());
        existente.setUnidadTiempo(vacuna.getUnidadTiempo());
        existente.setProximaDosis(vacuna.getProximaDosis());
        existente.setAplicadoPorUsuarioId(vacuna.getAplicadoPorUsuarioId());

        return vacunaRepository.save(existente);
    }
}
