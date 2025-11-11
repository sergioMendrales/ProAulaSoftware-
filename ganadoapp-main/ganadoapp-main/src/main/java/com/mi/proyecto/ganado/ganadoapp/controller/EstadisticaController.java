package com.mi.proyecto.ganado.ganadoapp.controller;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import com.mi.proyecto.ganado.ganadoapp.service.EstadisticasService;
import com.mi.proyecto.ganado.ganadoapp.service.GanadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
 public class EstadisticaController {

    @Autowired
    private GanadoService ganadoService;

    private final EstadisticasService estadisticasService;

    public EstadisticaController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }


    @GetMapping("ganado/estadisticas")
    public String verEstadisticas(Model model) {
        List<Ganado> listaGanado = ganadoService.listarGanados();

        long machos = listaGanado.stream().filter(g -> "Macho".equalsIgnoreCase(g.getSexo())).count();
        long hembras = listaGanado.stream().filter(g -> "Hembra".equalsIgnoreCase(g.getSexo())).count();
        long saludables = listaGanado.stream().filter(g -> "Saludable".equalsIgnoreCase(g.getEstadoSalud())).count();
        long enfermos = listaGanado.stream().filter(g -> "Enfermo".equalsIgnoreCase(g.getEstadoSalud())).count();

        Map<String, Long> porRaza = listaGanado.stream()
                .collect(Collectors.groupingBy(Ganado::getRaza, Collectors.counting()));

        model.addAttribute("machos", machos);
        model.addAttribute("hembras", hembras);
        model.addAttribute("saludables", saludables);
        model.addAttribute("enfermos", enfermos);
        model.addAttribute("porRaza", porRaza);


        return "estadisticas-ganado";
    }



 }
