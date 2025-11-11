package com.mi.proyecto.ganado.ganadoapp.service;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import com.mi.proyecto.ganado.ganadoapp.repository.GanadoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstadisticasService {

    private final GanadoRepository ganadoRepository;

    public EstadisticasService(GanadoRepository ganadoRepository) {
        this.ganadoRepository = ganadoRepository;
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> datos = new HashMap<>();

        datos.put("totalGanado", ganadoRepository.count());
        datos.put("totalMachos", ganadoRepository.countBySexo("Macho"));
        datos.put("totalHembras", ganadoRepository.countBySexo("Hembra"));
        datos.put("totalSanos", ganadoRepository.countByEstadoSalud("Saludable"));
        datos.put("totalEnfermos", ganadoRepository.countByEstadoSalud("Enfermo"));

        List<Ganado> todos = ganadoRepository.findAll();

        double promedioPeso = todos.stream()
                .mapToDouble(g -> g.getPesoKg() != null ? g.getPesoKg() : 0.0)
                .average()
                .orElse(0.0);

        datos.put("promedioPeso", promedioPeso);

        return datos;
    }
}
