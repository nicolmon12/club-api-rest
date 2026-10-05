package com.futbol.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entrenador del club.
 * Relación Uno a Uno: un Club tiene UN Entrenador y un Entrenador pertenece a UN Club.
 * El Entrenador NO conoce al Club (relación unidireccional); la dueña es Club.
 */
@Document(collection = "entrenadores")
public class Entrenador {

    @Id
    private String id;
    private String nombre;
    private String apellido;
    private int edad;
    private String nacionalidad;

    public Entrenador() {
    }

    public Entrenador(String nombre, String apellido, int edad, String nacionalidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.nacionalidad = nacionalidad;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}
