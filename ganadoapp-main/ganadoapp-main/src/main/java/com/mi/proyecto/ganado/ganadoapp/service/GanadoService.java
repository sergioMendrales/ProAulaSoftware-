package com.mi.proyecto.ganado.ganadoapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import com.mi.proyecto.ganado.ganadoapp.model.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.repository.GanaderoRepository;
import com.mi.proyecto.ganado.ganadoapp.repository.GanadoRepository;

@Service
public class GanadoService {

    @Autowired
    private GanadoRepository ganadoRepository;

    @Autowired
    private GanaderoRepository ganaderoRepository;


    public List<Ganado> listarGanados() {
        return ganadoRepository.findAll();
    }

    public Optional<Ganado> obtenerPorId(String id) {
        return ganadoRepository.findById(id);
    }

    public Ganado guardarGanado(Ganado ganado) {
        return ganadoRepository.save(ganado);
    }

    public void eliminarGanado(String id) {
        ganadoRepository.deleteById(id);
    }

    public List<Ganado> listarPorPropietario(String propietarioId) {
        return ganadoRepository.findByPropietarioId(propietarioId);
    }

    public Ganado obtenerPorCodigoOficial(String codigoOficial) {
        return ganadoRepository.findByCodigoOficial(codigoOficial);
    }

    public Ganado actualizarGanado(Ganado ganadoActualizado) {
        if (ganadoActualizado.getId() == null) {
            throw new RuntimeException("Id de ganado es null");
        }
        return ganadoRepository.save(ganadoActualizado);
    }

    public Ganado agregarVacuna(Ganado ganado, Vacuna vacuna) {
        ganado.agregarVacunaAlCarnet(vacuna);
        return ganadoRepository.save(ganado);
    }


    public int asignarPropietariosPorMarca() {
        List<Ganado> todos = ganadoRepository.findAll();
        List<com.mi.proyecto.ganado.ganadoapp.model.Ganadero> ganaderos = ganaderoRepository.findAll();
        int updated = 0;

        for (Ganado g : todos) {
            if (g.getPropietarioId() != null && !g.getPropietarioId().trim().isEmpty()) continue;
            String codigo = g.getCodigoOficial();
            if (codigo == null) continue;

            for (com.mi.proyecto.ganado.ganadoapp.model.Ganadero ga : ganaderos) {
                String marca = ga.getMarcaRegistro();
                if (marca == null || marca.trim().isEmpty()) continue;
                if (codigo.startsWith(marca + "-")) {
                    g.setPropietarioId(ga.getId());
                    ganadoRepository.save(g);
                    updated++;
                    break;
                }
            }
        }

        return updated;
    }
}


