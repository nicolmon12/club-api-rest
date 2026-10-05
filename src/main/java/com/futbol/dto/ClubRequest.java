package com.futbol.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON que recibe la API para crear/actualizar un Club.
 * Se envían solo los IDs de los documentos relacionados, por ejemplo:
 *
 * {
 *   "nombre": "Millonarios",
 *   "entrenadorId": "66f...a1",
 *   "asociacionId": "66f...b2",
 *   "jugadoresIds": ["66f...c3", "66f...c4"],
 *   "competicionesIds": ["66f...d5"]
 * }
 */
public class ClubRequest {

    private String nombre;
    private String entrenadorId;
    private String asociacionId;
    private List<String> jugadoresIds = new ArrayList<>();
    private List<String> competicionesIds = new ArrayList<>();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEntrenadorId() {
        return entrenadorId;
    }

    public void setEntrenadorId(String entrenadorId) {
        this.entrenadorId = entrenadorId;
    }

    public String getAsociacionId() {
        return asociacionId;
    }

    public void setAsociacionId(String asociacionId) {
        this.asociacionId = asociacionId;
    }

    public List<String> getJugadoresIds() {
        return jugadoresIds;
    }

    public void setJugadoresIds(List<String> jugadoresIds) {
        this.jugadoresIds = jugadoresIds;
    }

    public List<String> getCompeticionesIds() {
        return competicionesIds;
    }

    public void setCompeticionesIds(List<String> competicionesIds) {
        this.competicionesIds = competicionesIds;
    }
}
