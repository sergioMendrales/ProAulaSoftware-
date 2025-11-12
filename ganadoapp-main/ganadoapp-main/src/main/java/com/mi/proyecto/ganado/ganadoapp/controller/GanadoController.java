package com.mi.proyecto.ganado.ganadoapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import com.mi.proyecto.ganado.ganadoapp.model.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.service.GanadoService;
import com.mi.proyecto.ganado.ganadoapp.service.UsuarioService;
import com.mi.proyecto.ganado.ganadoapp.service.VacunaService;

@Controller
@RequestMapping("/ganado")
public class GanadoController {

    @Autowired
    private GanadoService ganadoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private VacunaService vacunaService;

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("ganado", new Ganado());
        return "registro-ganado";
    }

    @PostMapping("/guardar")
    public String guardarGanado(@ModelAttribute Ganado ganado) {

        String propietarioAsignado = null;
        if (ganado.getCodigoOficial() != null && !ganado.getCodigoOficial().isEmpty()) {
            String[] partes = ganado.getCodigoOficial().split("-");
            if (partes.length > 0) {
                String marca = partes[0].toUpperCase();
                try {
                    var usuarioPorMarca = usuarioService.buscarPorMarca(marca);
                    if (usuarioPorMarca.isPresent()) {
                        ganado.setPropietarioId(usuarioPorMarca.get().getId());
                        propietarioAsignado = usuarioPorMarca.get().getId();
                        System.out.println("[GANADO] Propietario asignado por marca: " + marca + " -> " + ganado.getPropietarioId());
                    } else {
                        System.out.println("[WARN] No se encontró ganadero con marca: " + marca);
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Error al buscar propietario por marca: " + e.getMessage());
                }
            }
        }


        if (propietarioAsignado == null) {
            try {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                if (username != null && !username.isEmpty() && !"anonymousUser".equals(username)) {
                    usuarioService.buscarPorEmail(username).ifPresent(u -> {
                        ganado.setPropietarioId(u.getId());
                        System.out.println("[GANADO] Propietario asignado desde usuario autenticado: " + u.getEmail());
                    });
                }
            } catch (Exception e) {
                System.err.println("[WARN] No se pudo asignar propietario al ganado: " + e.getMessage());
            }
        }

        ganadoService.guardarGanado(ganado);
        return "redirect:/ganado/detalle/" + ganado.getId();
    }

    @GetMapping("/lista")
    public String listarGanado(Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Ganado> ganados = new java.util.ArrayList<>();
        
        if (username != null && !username.isEmpty() && !"anonymousUser".equals(username)) {
            usuarioService.buscarPorEmail(username).ifPresent(usuario -> {
                List<Ganado> ganadadosPorPropietario = ganadoService.listarPorPropietario(usuario.getId());
                if (ganadadosPorPropietario != null) {
                    ganados.addAll(ganadadosPorPropietario);
                }
            });
        }
        
        model.addAttribute("ganados", ganados);
        return "lista-ganado";
    }

    @GetMapping("/detalle/{id}")
    public String detalleGanado(@PathVariable("id") String id, Model model) {
        Ganado ganado = ganadoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null && !username.isEmpty() && !"anonymousUser".equals(username)) {
            var usuarioActual = usuarioService.buscarPorEmail(username);
            if (usuarioActual.isPresent()) {

                if (ganado.getPropietarioId() != null && !ganado.getPropietarioId().equals(usuarioActual.get().getId())) {
                    throw new RuntimeException("No tienes permisos para ver este ganado");
                }
            }
        }


        List<Vacuna> vacunasDelGanado = vacunaService.obtenerVacunasPorGanadoId(id);


        if (vacunasDelGanado == null || vacunasDelGanado.isEmpty()) {
            String codigoOficial = ganado.getCodigoOficial();
            if (codigoOficial != null && !codigoOficial.isEmpty()) {
                vacunasDelGanado = vacunaService.obtenerVacunasPorCodigoGanado(codigoOficial);
            }
        }

        model.addAttribute("ganado", ganado);
        model.addAttribute("vacuna", new Vacuna());
        model.addAttribute("vacunas", vacunasDelGanado);


        model.addAttribute("mostrarAccionesEdicion", true);

        return "detalle-ganado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarGanado(@PathVariable("id") String id) {

        Ganado ganado = ganadoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null && !username.isEmpty() && !"anonymousUser".equals(username)) {
            var usuarioActual = usuarioService.buscarPorEmail(username);
            if (usuarioActual.isPresent()) {
                if (ganado.getPropietarioId() != null && !ganado.getPropietarioId().equals(usuarioActual.get().getId())) {
                    throw new RuntimeException("No tienes permisos para eliminar este ganado");
                }
            }
        }
        
        ganadoService.eliminarGanado(id);
        return "redirect:/ganado/lista";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") String id, Model model) {
        Ganado ganado = ganadoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
        

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null && !username.isEmpty() && !"anonymousUser".equals(username)) {
            var usuarioActual = usuarioService.buscarPorEmail(username);
            if (usuarioActual.isPresent()) {
                if (ganado.getPropietarioId() != null && !ganado.getPropietarioId().equals(usuarioActual.get().getId())) {
                    throw new RuntimeException("No tienes permisos para editar este ganado");
                }
            }
        }
        
        model.addAttribute("ganado", ganado);
        return "editar-ganado";
    }

    @PostMapping("/actualizar")
    public String actualizarGanado(@ModelAttribute("ganado") Ganado ganado) {
        ganadoService.actualizarGanado(ganado);
        return "redirect:/ganado/lista";
    }
}
