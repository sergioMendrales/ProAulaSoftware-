package com.mi.proyecto.ganado.ganadoapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    @GetMapping("/")
    public String bienvenido() {

        return "Bienvenido";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
