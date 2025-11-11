package com.mi.proyecto.ganado.ganadoapp.controller;

import com.mi.proyecto.ganado.ganadoapp.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/debug")
public class DebugController {

    private final UsuarioService usuarioService;

    public DebugController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Endpoint para desarrollo: lista usuarios sin exponer contraseñas
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = usuarioService.listarTodos().stream()
                .map(u -> new UserDto(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.getLicencia(), u.getMarcaRegistro()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    public static class UserDto {
        public String id;
        public String nombre;
        public String email;
        public String rol;
        public String licencia;
        public String marcaRegistro;

        public UserDto(String id, String nombre, String email, String rol, String licencia, String marcaRegistro) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.rol = rol;
            this.licencia = licencia;
            this.marcaRegistro = marcaRegistro;
        }
    }
}
