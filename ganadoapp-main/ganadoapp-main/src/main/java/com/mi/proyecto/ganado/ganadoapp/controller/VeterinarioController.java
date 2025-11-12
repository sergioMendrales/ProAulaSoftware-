package com.mi.proyecto.ganado.ganadoapp.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import com.mi.proyecto.ganado.ganadoapp.model.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.service.GanadoService;
import com.mi.proyecto.ganado.ganadoapp.service.UsuarioService;
import com.mi.proyecto.ganado.ganadoapp.service.VacunaService;

@Controller
@RequestMapping("/veterinario")
public class VeterinarioController {

    private final GanadoService ganadoService;
    private final UsuarioService usuarioService;
    private final VacunaService vacunaService;

    public VeterinarioController(GanadoService ganadoService, UsuarioService usuarioService, VacunaService vacunaService) {
        this.ganadoService = ganadoService;
        this.usuarioService = usuarioService;
        this.vacunaService = vacunaService;
    }

    @GetMapping("/ganaderos")
    public String listarGanaderos(Model model) {

    List<com.mi.proyecto.ganado.ganadoapp.model.Usuario> ganaderos = usuarioService.listarTodos().stream()
        .filter(u -> u.getRol() != null && u.getRol().equalsIgnoreCase("GANADERO"))
        .toList();

    System.out.println("[DEBUG] Usuarios con rol GANADERO encontrados: " + ganaderos.size());
    model.addAttribute("ganaderos", ganaderos);
        return "veterinario-ganaderos";
    }

    @GetMapping("/ganadero/{id}")
    public String verGanaderoDetalle(@PathVariable String id, Model model) {
    com.mi.proyecto.ganado.ganadoapp.model.Usuario ganadero = usuarioService.buscarPorId(id)
        .orElseThrow(() -> new RuntimeException("Ganadero no encontrado"));


    List<Ganado> ganadoList = ganadoService.listarPorPropietario(id);

    model.addAttribute("ganadero", ganadero);
        model.addAttribute("ganados", ganadoList);
        model.addAttribute("conteo", ganadoList.size());
        return "veterinario-ganadero-detalle";
    }

    @GetMapping("/ganadero/{id}/vacuna")
    public String mostrarFormularioVacunaParaGanadero(@PathVariable String id,
                                                      @RequestParam(value = "selectedGanado", required = false) String selectedGanado,
                                                      Model model) {

        model.addAttribute("vacuna", new Vacuna());
        java.util.List<Ganado> ganados = ganadoService.listarPorPropietario(id);
        model.addAttribute("ganados", ganados);
        model.addAttribute("selectedGanadoId", selectedGanado);
        model.addAttribute("ganaderoId", id);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            usuarioService.buscarPorEmail(auth.getName()).ifPresent(u -> {
                model.addAttribute("currentUserId", u.getId());
                model.addAttribute("currentUserName", u.getNombre());
            });
        }

        return "vacuna-formulario-veterinario";
    }

    @GetMapping("/ganado/{id}")
    public String verGanadoComoVeterinario(@PathVariable String id, Model model) {
        Ganado ganado = ganadoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));


        java.util.List<Vacuna> vacunasDelGanado = vacunaService.obtenerVacunasPorGanadoId(id);
        if (vacunasDelGanado == null || vacunasDelGanado.isEmpty()) {
            String codigoOficial = ganado.getCodigoOficial();
            if (codigoOficial != null && !codigoOficial.isEmpty()) {
                vacunasDelGanado = vacunaService.obtenerVacunasPorCodigoGanado(codigoOficial);
            }
        }

        model.addAttribute("ganado", ganado);
        model.addAttribute("vacunas", vacunasDelGanado);

        model.addAttribute("mostrarAccionesEdicion", false);
        return "detalle-ganado";
    }


    @GetMapping("/migrate-data")
    public String migrateData() {
        long ganaderosMigraron = usuarioService.migrateGanaderos();
        long veterinariosMigraron = usuarioService.migrateVeterinarios();
        System.out.println("[MIGRATION] Ganaderos migrados: " + ganaderosMigraron + ", Veterinarios migrados: " + veterinariosMigraron);
        return "redirect:/veterinario/ganaderos";
    }


    @GetMapping("/fix-ganado-owners")
    public String fixGanadoOwners() {
        int fixed = ganadoService.asignarPropietariosPorMarca();
        System.out.println("[FIX] Ganados con propietario asignado: " + fixed);
        return "redirect:/veterinario/ganaderos";
    }
}


