package com.mi.proyecto.ganado.ganadoapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mi.proyecto.ganado.ganadoapp.model.Usuario;
import com.mi.proyecto.ganado.ganadoapp.service.UsuarioService;


@Controller
@RequestMapping("/registro")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/ganadero")
    public String registroGanaderoForm(Model model, @RequestParam(value = "error", required = false) String error) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        if (error != null) model.addAttribute("error", error);
        return "registro-ganadero";
    }

    @PostMapping("/ganadero")
    public String registrarGanadero(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuario.setRol("GANADERO");
            usuarioService.registrarUsuarioGanadero(usuario);
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro-ganadero";
        } catch (Exception ex) {
            model.addAttribute("error", "Error al registrar: " + ex.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro-ganadero";
        }
    }

    @GetMapping("/veterinario")
    public String registroVeterinarioForm(Model model, @RequestParam(value = "error", required = false) String error) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        if (error != null) model.addAttribute("error", error);
        return "registro-veterinario";
    }


    @PostMapping("/veterinario")
    public String registrarVeterinario(@ModelAttribute Usuario usuario, Model model, @RequestParam(value = "error", required = false) String error) {
        try {
            usuarioService.registrarVeterinario(usuario);
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro-veterinario";
        } catch (Exception ex) {
            model.addAttribute("error", "Error al registrar: " + ex.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro-veterinario";
        }
    }
}
