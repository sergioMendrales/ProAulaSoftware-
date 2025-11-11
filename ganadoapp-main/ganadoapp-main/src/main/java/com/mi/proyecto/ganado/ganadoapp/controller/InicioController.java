package com.mi.proyecto.ganado.ganadoapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    @GetMapping("/")
    public String bienvenido() {
        // Devolver el nombre de plantilla exactamente como existe en templates: "Bienvenido.html"
        return "Bienvenido";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
