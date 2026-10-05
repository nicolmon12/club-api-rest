package com.futbol.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * CLASE MAESTRA: aquí (y solo aquí) se colocan las relaciones.
 *
 * En JPA/SQL se usaría:            En MongoDB (Spring Data) se usa:
 *   @Entity @Table(name="clubes")    @Document(collection = "clubes")
 *   @OneToOne                        @DocumentReference  (un solo objeto)
 *   @OneToMany + @JoinColumn         @DocumentReference  (List<...>)
 *   @ManyToOne                       @DocumentReference  (un solo objeto)
 *   @ManyToMany                      @DocumentReference  (List<...>)
 *
 * @DocumentReference guarda en el documento "clubes" solo el _id del documento
 * referenciado (como una FK) y Spring lo resuelve automáticamente al leer.
 *
 * Carga EAGER vs LAZY: por defecto la referencia se carga de inmediato (EAGER).
 * Para carga perezosa se usaría @DocumentReference(lazy = true).
 */
@Document(collection = "clubes")
public class Club {

    @Id
    private String id;
    private String nombre;

    /** Relación Uno a Uno (@OneToOne): un club tiene un entrenador. */
    @DocumentReference
    private Entrenador entrenador;

    /** Relación Uno a Muchos (@OneToMany): un club tiene muchos jugadores. */
    @DocumentReference
    private List<Jugador> jugadores = new ArrayList<>();

    /** Relación Muchos a Uno (@ManyToOne): muchos clubes pertenecen a una asociación. */
    @DocumentReference
    private Asociacion asociacion;

    /** Relación Muchos a Muchos (@ManyToMany): un club participa en muchas competiciones. */
    @DocumentReference
    private List<Competicion> competiciones = new ArrayList<>();

    public Club() {
    }

    public Club(String nombre) {
        this.nombre = nombre;
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

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public Asociacion getAsociacion() {
        return asociacion;
    }

    public void setAsociacion(Asociacion asociacion) {
        this.asociacion = asociacion;
    }

    public List<Competicion> getCompeticiones() {
        return competiciones;
    }

    public void setCompeticiones(List<Competicion> competiciones) {
        this.competiciones = competiciones;
    }
}
