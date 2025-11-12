package com.mi.proyecto.ganado.ganadoapp.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("userEmail")
    public String addUserEmail(Principal principal, Authentication authentication) {

        if (principal != null) {
            return principal.getName();
        }


        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }

        return null;
    }
}
