package com.futbol.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.futbol.model.Club;
import com.futbol.model.Entrenador;
import com.futbol.model.Jugador;
import com.futbol.repository.ClubRepository;

/**
 * Reglas de las relaciones.
 *
 * MongoDB NO tiene llaves foráneas (FK) como SQL, así que las restricciones que
 * en JPA daría la base de datos las validamos aquí:
 *  - 1 a 1  : un entrenador solo puede estar en UN club.
 *  - 1 a N  : un jugador solo puede estar en UN club.
 *  - "foreign key": no se puede borrar un documento que un club está referenciando
 *    (equivalente al ON DELETE NO ACTION de SQL).
 */
@Service
public class RelacionService {

    private final ClubRepository clubRepository;

    public RelacionService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /** Club (distinto de clubIdExcluido) que ya tiene asignado ese entrenador. */
    public Optional<Club> clubConEntrenador(String entrenadorId, String clubIdExcluido) {
        return clubRepository.findAll().stream()
                .filter(c -> !Objects.equals(c.getId(), clubIdExcluido))
                .filter(c -> c.getEntrenador() != null && Objects.equals(c.getEntrenador().getId(), entrenadorId))
                .findFirst();
    }

    /** Club (distinto de clubIdExcluido) que ya tiene ese jugador en su plantilla. */
    public Optional<Club> clubConJugador(String jugadorId, String clubIdExcluido) {
        return clubRepository.findAll().stream()
                .filter(c -> !Objects.equals(c.getId(), clubIdExcluido))
                .filter(c -> c.getJugadores() != null && c.getJugadores().stream()
                        .anyMatch(j -> j != null && Objects.equals(j.getId(), jugadorId)))
                .findFirst();
    }

    public boolean asociacionEnUso(String asociacionId) {
        return clubRepository.findAll().stream()
                .anyMatch(c -> c.getAsociacion() != null && Objects.equals(c.getAsociacion().getId(), asociacionId));
    }

    public boolean competicionEnUso(String competicionId) {
        return clubRepository.findAll().stream()
                .anyMatch(c -> c.getCompeticiones() != null && c.getCompeticiones().stream()
                        .anyMatch(x -> x != null && Objects.equals(x.getId(), competicionId)));
    }

    /** Ids de entrenadores que ya están en otro club (para no ofrecerlos en el formulario). */
    public Set<String> entrenadoresOcupados(String clubIdExcluido) {
        return clubRepository.findAll().stream()
                .filter(c -> !Objects.equals(c.getId(), clubIdExcluido))
                .map(Club::getEntrenador)
                .filter(Objects::nonNull)
                .map(Entrenador::getId)
                .collect(Collectors.toSet());
    }

    /** Ids de jugadores que ya están en otro club. */
    public Set<String> jugadoresOcupados(String clubIdExcluido) {
        return clubRepository.findAll().stream()
                .filter(c -> !Objects.equals(c.getId(), clubIdExcluido))
                .filter(c -> c.getJugadores() != null)
                .flatMap(c -> c.getJugadores().stream())
                .filter(Objects::nonNull)
                .map(Jugador::getId)
                .collect(Collectors.toSet());
    }

    // ------------- Mapas para mostrar en las listas a qué club pertenece cada documento -------------

    /** id del entrenador -> nombre del club que dirige. */
    public Map<String, String> clubPorEntrenador() {
        Map<String, String> mapa = new HashMap<>();
        for (Club c : clubRepository.findAll()) {
            if (c.getEntrenador() != null) {
                mapa.put(c.getEntrenador().getId(), c.getNombre());
            }
        }
        return mapa;
    }

    /** id del jugador -> nombre de su club. */
    public Map<String, String> clubPorJugador() {
        Map<String, String> mapa = new HashMap<>();
        for (Club c : clubRepository.findAll()) {
            if (c.getJugadores() != null) {
                c.getJugadores().stream().filter(Objects::nonNull).forEach(j -> mapa.put(j.getId(), c.getNombre()));
            }
        }
        return mapa;
    }

    /** id de la asociación -> cantidad de clubes afiliados. */
    public Map<String, Integer> clubesPorAsociacion() {
        Map<String, Integer> mapa = new HashMap<>();
        for (Club c : clubRepository.findAll()) {
            if (c.getAsociacion() != null) {
                mapa.merge(c.getAsociacion().getId(), 1, Integer::sum);
            }
        }
        return mapa;
    }

    /** id de la competición -> cantidad de clubes participantes. */
    public Map<String, Integer> clubesPorCompeticion() {
        Map<String, Integer> mapa = new HashMap<>();
        for (Club c : clubRepository.findAll()) {
            if (c.getCompeticiones() != null) {
                c.getCompeticiones().stream().filter(Objects::nonNull)
                        .forEach(x -> mapa.merge(x.getId(), 1, Integer::sum));
            }
        }
        return mapa;
    }
}
