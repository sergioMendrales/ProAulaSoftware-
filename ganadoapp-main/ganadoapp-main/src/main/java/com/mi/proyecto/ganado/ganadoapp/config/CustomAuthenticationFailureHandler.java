package com.mi.proyecto.ganado.ganadoapp.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String loginAs = request.getParameter("loginAs");
        String msg = exception.getMessage() != null ? exception.getMessage() : "error";
        LOG.warn("Authentication failure for user='{}' requestedRole='{}' reason='{}'", username, loginAs, msg);
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/login?error=" + encoded);
    }
}
