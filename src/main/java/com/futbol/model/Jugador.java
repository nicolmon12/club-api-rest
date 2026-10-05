package com.futbol.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Jugador de la plantilla.
 * Relación Uno a Muchos: un Club tiene MUCHOS Jugadores, cada Jugador pertenece a UN Club.
 */
@Document(collection = "jugadores")
public class Jugador {

    @Id
    private String id;
    private String nombre;
    private String apellido;
    private int numero;
    private String posicion;

    public Jugador() {
    }

    public Jugador(String nombre, String apellido, int numero, String posicion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.posicion = posicion;
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

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }
}
