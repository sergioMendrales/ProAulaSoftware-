package com.mi.proyecto.ganado.ganadoapp.model;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;


public class Registro {

    private String nombreCompleto;
    private String email;
    private String password;
    private String confirmarPassword;
    private String rol;
    private String telefono;
    private String direccion;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;


    public Registro() {
        this.fechaRegistro = LocalDate.now();
    }


    public Registro(String nombreCompleto, String email, String password, String confirmarPassword,
                    String rol, String telefono, String direccion, LocalDate fechaRegistro) {
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.confirmarPassword = confirmarPassword;
        this.rol = rol;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDate.now();
    }



    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
