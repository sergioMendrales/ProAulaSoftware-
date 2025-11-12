package com.mi.proyecto.ganado.ganadoapp.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Redirige al usuario tras login según el parámetro "loginAs" enviado desde el formulario
 * y las autoridades que tenga el usuario. Si el usuario no tiene la autoridad solicitada
 * se redirige a la página por defecto (/ganado/lista).
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String loginAs = request.getParameter("loginAs");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isVeterinario = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VETERINARIO"));
        boolean isGanadero = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_GANADERO"));

        String targetUrl = "/ganado/lista"; // fallback

        if (loginAs != null) {
            String roleReq = loginAs.toUpperCase();
            if ("VETERINARIO".equals(roleReq)) {
                if (isVeterinario) {
                    targetUrl = "/veterinario/ganaderos";
                } else {
                    // rol solicitado no coincide -> invalidar login y regresar con error
                    if (request.getSession(false) != null) {
                        try { request.getSession(false).invalidate(); } catch (Exception ignored) {}
                    }
                    response.sendRedirect(request.getContextPath() + "/login?error=roleMismatch");
                    return;
                }
            } else if ("GANADERO".equals(roleReq)) {
                if (isGanadero) {
                    targetUrl = "/ganado/lista";
                } else {
                    if (request.getSession(false) != null) {
                        try { request.getSession(false).invalidate(); } catch (Exception ignored) {}
                    }
                    response.sendRedirect(request.getContextPath() + "/login?error=roleMismatch");
                    return;
                }
            }
        } else {
            // si no se envía preferencia, dirigir según el rol real
            if (isVeterinario) targetUrl = "/veterinario/ganaderos";
            else if (isGanadero) targetUrl = "/ganado/lista";
        }

        response.sendRedirect(request.getContextPath() + targetUrl);
    }
}
