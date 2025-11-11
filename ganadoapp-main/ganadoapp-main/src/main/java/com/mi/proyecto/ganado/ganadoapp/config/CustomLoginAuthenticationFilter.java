package com.mi.proyecto.ganado.ganadoapp.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Extiende el filtro estándar para inyectar el parámetro "loginAs" dentro del
 * objeto UsernamePasswordAuthenticationToken.details antes de que llegue al provider.
 */
public class CustomLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        // establecer el detalle como el valor del parámetro loginAs para que el AuthenticationProvider pueda leerlo
        String loginAs = request.getParameter("loginAs");
        if (loginAs != null) {
            authRequest.setDetails(loginAs);
        } else {
            super.setDetails(request, authRequest);
        }
    }
}
