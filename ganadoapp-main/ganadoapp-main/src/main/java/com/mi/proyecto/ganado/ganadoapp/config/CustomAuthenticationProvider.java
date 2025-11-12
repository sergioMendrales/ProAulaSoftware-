package com.mi.proyecto.ganado.ganadoapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mi.proyecto.ganado.ganadoapp.model.Usuario;
import com.mi.proyecto.ganado.ganadoapp.repository.UsuarioRepository;

/**
 * Provider que valida primero que el rol solicitado (en details) coincida con
 * el rol real del usuario antes de delegar en DaoAuthenticationProvider.
 */
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final UsuarioRepository usuarioRepository;
    private static final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        // loginAs is placed in authentication.getDetails() by the custom filter
        Object details = authentication.getDetails();
        String requestedRole = details != null ? details.toString().toUpperCase() : null;

    LOG.info("Authenticating user='{}' requestedRole='{}'", username, requestedRole);

        // Lookup usuario and check role if requestedRole provided
        if (requestedRole != null && !requestedRole.isBlank()) {
            Usuario u = usuarioRepository.findByEmail(username).orElse(null);
            if (u == null) {
                LOG.warn("Usuario '{}' no encontrado en repository durante pre-role-check", username);
                // let DaoAuthenticationProvider handle user-not-found
            } else {
                String actualRole = u.getRol() != null ? u.getRol().toUpperCase() : "GANADERO";
                LOG.info("Usuario '{}' tiene rol='{}' (comparando con solicitado='{}')", username, actualRole, requestedRole);
                if (requestedRole.equals("VETERINARIO") && !actualRole.equals("VETERINARIO")) {
                    LOG.warn("Rejecting authentication for '{}' because requestedRole=VETERINARIO but actualRole={}", username, actualRole);
                    throw new BadCredentialsException("roleMismatch");
                }
                if (requestedRole.equals("GANADERO") && !actualRole.equals("GANADERO")) {
                    LOG.warn("Rejecting authentication for '{}' because requestedRole=GANADERO but actualRole={}", username, actualRole);
                    throw new BadCredentialsException("roleMismatch");
                }
            }
        }

        // If role matches or wasn't requested, delegate to parent for normal authentication
        return super.authenticate(authentication);
    }
}
