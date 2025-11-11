package com.mi.proyecto.ganado.ganadoapp.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "veterinarios")
public class Veterinario extends Usuario {

    public Veterinario() {
        super();
    }

    public Veterinario(String nombre, String correo, String password) {
        super(nombre, correo, password, "VETERINARIO");
    }



}

