package com.futbol.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Asociación / federación (ej: FCF - Federación Colombiana de Fútbol).
 * Relación Muchos a Uno: muchos Clubes pertenecen a UNA Asociación.
 * (La referencia se guarda en la clase maestra Club).
 */
@Document(collection = "asociaciones")
public class Asociacion {

    @Id
    private String id;
    private String nombre;
    private String pais;
    private String presidente;

    public Asociacion() {
    }

    public Asociacion(String nombre, String pais, String presidente) {
        this.nombre = nombre;
        this.pais = pais;
        this.presidente = presidente;
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
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getPresidente() {
        return presidente;
    }

    public void setPresidente(String presidente) {
        this.presidente = presidente;
    }
}
