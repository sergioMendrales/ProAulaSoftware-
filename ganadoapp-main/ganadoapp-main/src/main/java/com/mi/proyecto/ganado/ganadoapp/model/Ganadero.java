package com.mi.proyecto.ganado.ganadoapp.model;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "ganaderos")
public class Ganadero extends Usuario {

    private List<String> ganadoIds = new ArrayList<>();

    public Ganadero() {
        super();
    }

    public Ganadero(String nombre, String correo, String password) {
        super(nombre, correo, password, "GANADERO");
    }

    public List<String> getGanadoIds() {
        return ganadoIds;
    }

    public void setGanadoIds(List<String> ganadoIds) {
        this.ganadoIds = ganadoIds;
    }

    public void agregarGanadoId(String ganadoId) {
        if (this.ganadoIds == null) {
            this.ganadoIds = new ArrayList<>();
        }
        this.ganadoIds.add(ganadoId);
    }

    public void removerGanadoId(String ganadoId) {
        if (this.ganadoIds != null) {
            this.ganadoIds.remove(ganadoId);
        }
    }
}
