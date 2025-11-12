package com.mi.proyecto.ganado.ganadoapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "ganado")
public class Ganado {

    @Id
    private String id;
    private String codigoOficial; // código generado (marca/padre/mes/año/cons)
    private String color;
    private String raza;
    private Integer edadMeses;
    private Double pesoKg;
    private String sexo;
    private String estadoSalud;
    private String propietarioId;
    private List<Vacuna> carnetVacunas = new ArrayList<>();
    private LocalDate fechaNacimiento;

    public Ganado() {
    }

    public Ganado(String codigoOficial, String color, String raza, Integer edadMeses, Double pesoKg,
                  String sexo, String estadoSalud, String propietarioId) {
        this.codigoOficial = codigoOficial;
        this.color = color;
        this.raza = raza;
        this.edadMeses = edadMeses;
        this.pesoKg = pesoKg;
        this.sexo = sexo;
        this.estadoSalud = estadoSalud;
        this.propietarioId = propietarioId;
    }

    public String generarCodigoOficial(String marcaFinca, String codigoPadre, int mesNacimiento, int anioNacimiento, int consecutivo) {
        String marca = (marcaFinca == null || marcaFinca.isEmpty()) ? "XXX" : marcaFinca.toUpperCase();
        String padre = (codigoPadre == null || codigoPadre.isEmpty()) ? "00" : codigoPadre.toUpperCase();
        String mes = String.format("%02d", mesNacimiento);
        String anio = String.format("%02d", anioNacimiento % 100);
        String cons = String.format("%03d", consecutivo);
        return String.format("%s-%s-%s-%s-%s", marca, padre, mes, anio, cons);
    }

    public void asignarCodigoSiNoExiste(String marcaFinca, String codigoPadre, int mesNacimiento, int anioNacimiento, int consecutivo) {
        if (this.codigoOficial == null || this.codigoOficial.isEmpty()) {
            this.codigoOficial = generarCodigoOficial(marcaFinca, codigoPadre, mesNacimiento, anioNacimiento, consecutivo);
        }
    }

    public void agregarVacunaAlCarnet(Vacuna vacuna) {
        if (this.carnetVacunas == null) {
            this.carnetVacunas = new ArrayList<>();
        }
        this.carnetVacunas.add(vacuna);
    }



    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoOficial() {
        return this.codigoOficial;
    }

    public void setCodigoOficial(String codigoOficial) {
        this.codigoOficial = codigoOficial;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRaza() {
        return this.raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Integer getEdadMeses() {
        return this.edadMeses;
    }

    public void setEdadMeses(Integer edadMeses) {
        this.edadMeses = edadMeses;
    }

    public Double getPesoKg() {
        return this.pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public String getSexo() {
        return this.sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstadoSalud() {
        return this.estadoSalud;
    }

    public void setEstadoSalud(String estadoSalud) {
        this.estadoSalud = estadoSalud;
    }

    public String getPropietarioId() {
        return this.propietarioId;
    }

    public void setPropietarioId(String propietarioId) {
        this.propietarioId = propietarioId;
    }

    public List<Vacuna> getCarnetVacunas() {
        return this.carnetVacunas;
    }

    public void setCarnetVacunas(List<Vacuna> carnetVacunas) {
        this.carnetVacunas = carnetVacunas;
    }

    public LocalDate getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaNacimientoFormateada() {
        if (this.fechaNacimiento == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.fechaNacimiento.format(fmt);
    }
}
