package com.mi.proyecto.ganado.ganadoapp.config;

import com.mi.proyecto.ganado.ganadoapp.model.Usuario;
import com.mi.proyecto.ganado.ganadoapp.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsConfig implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));


        String rol = usuario.getRol() != null ? usuario.getRol().toUpperCase() : "GANADERO";


        if (!rol.equals("GANADERO") && !rol.equals("VETERINARIO")) {
            rol = "GANADERO";
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
