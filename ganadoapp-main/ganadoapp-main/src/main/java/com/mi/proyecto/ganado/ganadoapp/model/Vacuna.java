package com.mi.proyecto.ganado.ganadoapp.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vacunas")
public class Vacuna {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaAplicacion;
    private LocalDate proximaAplicacion;
    private String observaciones;
    private String ganadoId;
    private String codigoGanado;
    private String aplicadoPorUsuarioId;
    // Campos adicionales para soporte de intervalos y próxima dosis desde la UI
    private Integer intervaloCantidad;
    private String unidadTiempo;
    private LocalDate proximaDosis;



    public Vacuna() {
    }

    public Vacuna(String nombre, String descripcion, LocalDate fechaAplicacion, String ganadoId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaAplicacion = fechaAplicacion;
        this.ganadoId = ganadoId;
        this.proximaAplicacion = calcularProximaAplicacion(nombre, fechaAplicacion);
    }

    public Vacuna(String id, String nombre, String descripcion, LocalDate fechaAplicacion,
                  LocalDate proximaAplicacion, String observaciones, String ganadoId,
                  String codigoGanado, String aplicadoPorUsuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaAplicacion = fechaAplicacion;
        this.proximaAplicacion = proximaAplicacion;
        this.observaciones = observaciones;
        this.ganadoId = ganadoId;
        this.codigoGanado = codigoGanado;
        this.aplicadoPorUsuarioId = aplicadoPorUsuarioId;
    }

    // Getters/Setters para los nuevos campos usados por la plantilla de edición
    public Integer getIntervaloCantidad() {
        return intervaloCantidad;
    }

    public void setIntervaloCantidad(Integer intervaloCantidad) {
        this.intervaloCantidad = intervaloCantidad;
    }

    public String getUnidadTiempo() {
        return unidadTiempo;
    }

    public void setUnidadTiempo(String unidadTiempo) {
        this.unidadTiempo = unidadTiempo;
    }

    public LocalDate getProximaDosis() {
        return proximaDosis;
    }

    public void setProximaDosis(LocalDate proximaDosis) {
        this.proximaDosis = proximaDosis;
    }



    public static LocalDate calcularProximaAplicacion(String nombre, LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        String n = (nombre == null) ? "" : nombre.toLowerCase().trim();
        switch (n) {
            case "fiebre aftosa":
            case "fiebreaftosa":
                return fecha.plusMonths(6);
            case "brucelosis":
                return fecha.plusYears(1);
            case "carbunco":
            case "carbunco sintomatico":
                return fecha.plusYears(1);
            case "rabia":
            case "rabia bovina":
                return fecha.plusYears(1);
            case "leptospirosis":
                return fecha.plusMonths(6);
            default:
                return fecha.plusYears(1);
        }
    }

    public String getProximaAplicacionFormateada() {
        if (proximaAplicacion == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return proximaAplicacion.format(fmt);
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        if (this.fechaAplicacion != null) {
            this.proximaAplicacion = calcularProximaAplicacion(nombre, this.fechaAplicacion);
        }
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
        this.proximaAplicacion = calcularProximaAplicacion(this.nombre, fechaAplicacion);
    }

    public LocalDate getProximaAplicacion() {
        return proximaAplicacion;
    }

    public void setProximaAplicacion(LocalDate proximaAplicacion) {
        this.proximaAplicacion = proximaAplicacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getGanadoId() {
        return ganadoId;
    }

    public void setGanadoId(String ganadoId) {
        this.ganadoId = ganadoId;
    }

    public String getCodigoGanado() {
        return codigoGanado;
    }

    public void setCodigoGanado(String codigoGanado) {
        this.codigoGanado = codigoGanado;
    }

    public String getAplicadoPorUsuarioId() {
        return aplicadoPorUsuarioId;
    }

    public void setAplicadoPorUsuarioId(String aplicadoPorUsuarioId) {
        this.aplicadoPorUsuarioId = aplicadoPorUsuarioId;
    }
}
