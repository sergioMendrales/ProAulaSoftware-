package com.mi.proyecto.ganado.ganadoapp.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import com.mi.proyecto.ganado.ganadoapp.model.Vacuna;
import com.mi.proyecto.ganado.ganadoapp.service.GanadoService;
import com.mi.proyecto.ganado.ganadoapp.service.UsuarioService;
import com.mi.proyecto.ganado.ganadoapp.service.VacunaService;

@Controller
@RequestMapping("/vacuna")
public class VacunaController {

    private final VacunaService vacunaService;
    private final GanadoService ganadoService;
    private final UsuarioService usuarioService;

    public VacunaController(VacunaService vacunaService, GanadoService ganadoService, UsuarioService usuarioService) {
        this.vacunaService = vacunaService;
        this.ganadoService = ganadoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registrar")
    public String mostrarFormularioVacuna(Model model,
                                         @RequestParam(value = "codigoGanado", required = false) String codigoGanado) {
        model.addAttribute("vacuna", new Vacuna());
        
        // Obtener usuario actual para detectar su rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = null;
        String userId = null;
        
        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            var usuarioOpt = usuarioService.buscarPorEmail(auth.getName());
            if (usuarioOpt.isPresent()) {
                var usuario = usuarioOpt.get();
                userRole = usuario.getRol();
                userId = usuario.getId();
                model.addAttribute("currentUserId", usuario.getId());
                model.addAttribute("currentUserName", usuario.getNombre());
                model.addAttribute("currentUserRole", userRole);
            }
        }
        
        // Si es veterinario, mostrar todos los ganados
        // Si es ganadero, mostrar solo sus ganados
        java.util.List<Ganado> ganados;
        if ("VETERINARIO".equalsIgnoreCase(userRole)) {
            ganados = ganadoService.listarGanados();
        } else if ("GANADERO".equalsIgnoreCase(userRole) && userId != null) {
            ganados = ganadoService.listarPorPropietario(userId);
        } else {
            ganados = ganadoService.listarGanados();
        }
        
        model.addAttribute("ganados", ganados);
        model.addAttribute("selectedGanadoId", codigoGanado);
        return "vacuna-formulario";
    }

    @PostMapping("/guardar")
    public String guardarVacuna(@ModelAttribute Vacuna vacuna,
                                @RequestParam("codigoGanado") String codigoGanado,
                                @RequestParam(value = "aplicadoPorUsuarioId", required = false) String aplicadoPorUsuarioId,
                                RedirectAttributes redirectAttributes) {

        // Asegurarnos de que ganadoId está establecido
        if (codigoGanado == null || codigoGanado.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: ID de ganado no proporcionado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/ganado/lista";
        }
        vacuna.setGanadoId(codigoGanado);
        vacuna.setAplicadoPorUsuarioId(aplicadoPorUsuarioId);
        String ganadoId = vacuna.getGanadoId();
        
        // Detectar el rol del usuario actual para redirigir correctamente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String returnUrl = "/ganado/detalle/" + ganadoId; // por defecto, ganadero
        
        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            var usuarioOpt = usuarioService.buscarPorEmail(auth.getName());
            if (usuarioOpt.isPresent()) {
                String userRole = usuarioOpt.get().getRol();
                // Si es veterinario, redirigir a la vista del veterinario
                if ("VETERINARIO".equalsIgnoreCase(userRole)) {
                    returnUrl = "/veterinario/ganado/" + ganadoId;
                }
            }
        }
        
        try {
            Vacuna vacunaGuardada = vacunaService.registrarVacuna(vacuna);
            // Si el servicio añadió observaciones (p. ej. recomendaciones por peso/edad),
            // presentamos una alerta de advertencia junto al mensaje de éxito.
            if (vacunaGuardada.getObservaciones() != null && !vacunaGuardada.getObservaciones().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "Vacuna registrada correctamente. " + vacunaGuardada.getObservaciones());
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Vacuna registrada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            }
            return "redirect:" + returnUrl;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:" + returnUrl;
        } catch (RuntimeException ex) {
            // Capturamos excepciones específicas (por ejemplo: Ganado no compatible)
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:" + returnUrl;
        } catch (Exception ex) {
            // Capturamos excepciones no previstas
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar vacuna: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/ganado/lista"; // redirigimos a lista para evitar URL inválida
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVacuna(@PathVariable String id) {
        vacunaService.eliminarVacuna(id);
        return "redirect:/vacuna-todas";
    }
       @GetMapping("/eliminar/{id}/{ganadoId}")
       public String eliminarVacunaDesdeGanado(@PathVariable String id, @PathVariable String ganadoId,
                                               RedirectAttributes redirectAttributes) {
           // Obtener la vacuna antes de eliminarla para conocer el ganado asociado
           Vacuna vacuna = vacunaService.obtenerPorId(id).orElse(null);
           String ganadoIdReal = (vacuna != null) ? vacuna.getGanadoId() : ganadoId;
       
           // Intentar eliminar y redirigir al lugar correcto. Si el usuario autenticado
           // es el propietario del ganado, redirigimos a la vista del ganadero; en caso
           // contrario (por ejemplo un veterinario que esté viendo el registro), redirigimos
           // a la vista del veterinario. Esto evita errores de permisos al redirigir a
           // /ganado/detalle cuando el usuario no es el propietario.
           try {
               vacunaService.eliminarVacuna(id);
               redirectAttributes.addFlashAttribute("mensaje", "Vacuna eliminada correctamente");
               redirectAttributes.addFlashAttribute("tipoMensaje", "success");

               // Buscar el ganado para comprobar propietario
               var ganadoOpt = ganadoService.obtenerPorId(ganadoIdReal);
               String propietarioId = null;
               if (ganadoOpt.isPresent()) {
                   propietarioId = ganadoOpt.get().getPropietarioId();
               }

               // Obtener usuario autenticado
               Authentication auth = SecurityContextHolder.getContext().getAuthentication();
               String usuarioId = null;
               if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
                   var usuarioOpt = usuarioService.buscarPorEmail(auth.getName());
                   if (usuarioOpt.isPresent()) {
                       usuarioId = usuarioOpt.get().getId();
                   }
               }

               // Si el usuario autenticado es el propietario del ganado, ir a la vista de ganadero
               if (usuarioId != null && propietarioId != null && usuarioId.equals(propietarioId)) {
                   return "redirect:/ganado/detalle/" + ganadoIdReal;
               }

               // En cualquier otro caso (por ejemplo veterinario) redirigir a la vista veterinario
               return "redirect:/veterinario/ganado/" + ganadoIdReal;
           } catch (Exception ex) {
               redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar vacuna: " + ex.getMessage());
               redirectAttributes.addFlashAttribute("tipoMensaje", "danger");

               // En caso de error también intentamos redirigir con la misma lógica
               var ganadoOpt = ganadoService.obtenerPorId(ganadoIdReal);
               String propietarioId = null;
               if (ganadoOpt.isPresent()) {
                   propietarioId = ganadoOpt.get().getPropietarioId();
               }

               Authentication auth = SecurityContextHolder.getContext().getAuthentication();
               String usuarioId = null;
               if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
                   var usuarioOpt = usuarioService.buscarPorEmail(auth.getName());
                   if (usuarioOpt.isPresent()) {
                       usuarioId = usuarioOpt.get().getId();
                   }
               }

               if (usuarioId != null && propietarioId != null && usuarioId.equals(propietarioId)) {
                   return "redirect:/ganado/detalle/" + ganadoIdReal;
               }
               return "redirect:/veterinario/ganado/" + ganadoIdReal;
           }
       }

    @GetMapping("/todas")
    public String mostrarTodasLasVacunas(Model model) {
        List<Vacuna> todas = vacunaService.listarTodas();
        model.addAttribute("vacunas", todas);
        return "vacunas-todas";
    }

    @GetMapping("/editar/{id}")
    public String editarVacuna(@PathVariable String id, Model model) {
        Vacuna vacuna = vacunaService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
        
        // Detectar el rol del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = null;
        
        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            var usuarioOpt = usuarioService.buscarPorEmail(auth.getName());
            if (usuarioOpt.isPresent()) {
                userRole = usuarioOpt.get().getRol();
                model.addAttribute("currentUserId", usuarioOpt.get().getId());
                model.addAttribute("currentUserName", usuarioOpt.get().getNombre());
            }
        }
        
        model.addAttribute("vacuna", vacuna);
        model.addAttribute("userRole", userRole);
        
        // Si es veterinario, usar la plantilla del veterinario
        if ("VETERINARIO".equalsIgnoreCase(userRole)) {
            return "vacuna-formulario-editar-veterinario";
        }
        return "vacuna-formulario-editar";
    }

    @PostMapping("/actualizar")
    public String actualizarVacuna(@ModelAttribute Vacuna vacuna,
                                   @RequestParam(value = "codigoGanado", required = false) String codigoGanado,
                                   RedirectAttributes redirectAttributes) {
        try {
            vacunaService.actualizarVacuna(vacuna);
            redirectAttributes.addFlashAttribute("mensaje", "Vacuna actualizada correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            String ganadoId = codigoGanado != null ? codigoGanado : vacuna.getGanadoId();
            
            // Detectar el rol del usuario autenticado para redirigir correctamente
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userRole = null;
            
            if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
                var usuarioOpt = usuarioService.buscarPorEmail(auth.getName());
                if (usuarioOpt.isPresent()) {
                    userRole = usuarioOpt.get().getRol();
                }
            }
            
            // Si es veterinario, redirigir a la vista veterinario del ganado
            if ("VETERINARIO".equalsIgnoreCase(userRole)) {
                return "redirect:/veterinario/ganado/" + ganadoId;
            }
            return "redirect:/ganado/detalle/" + ganadoId;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar vacuna: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/vacuna/editar/" + vacuna.getId();
        }
    }
}
