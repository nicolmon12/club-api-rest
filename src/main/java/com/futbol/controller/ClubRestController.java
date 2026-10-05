package com.futbol.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.futbol.dto.ClubRequest;
import com.futbol.model.Club;
import com.futbol.model.Competicion;
import com.futbol.model.Jugador;
import com.futbol.repository.AsociacionRepository;
import com.futbol.repository.ClubRepository;
import com.futbol.repository.CompeticionRepository;
import com.futbol.repository.EntrenadorRepository;
import com.futbol.repository.JugadorRepository;
import com.futbol.service.RelacionService;

/**
 * API REST de la CLASE MAESTRA Club (colección "clubes").
 *
 *  GET    /api/clubes                               -> listar (con relaciones resueltas)
 *  GET    /api/clubes/{id}                          -> buscar uno
 *  POST   /api/clubes                               -> crear   (ClubRequest con ids)
 *  PUT    /api/clubes/{id}                          -> actualizar
 *  DELETE /api/clubes/{id}                          -> eliminar
 *  GET    /api/clubes/{id}/jugadores                -> jugadores del club (1 a N)
 *  POST   /api/clubes/{id}/jugadores/{jugadorId}    -> agregar jugador al club
 *  DELETE /api/clubes/{id}/jugadores/{jugadorId}    -> quitar jugador del club
 */
@RestController
@RequestMapping("/api/clubes")
public class ClubRestController {

    private final ClubRepository clubRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final JugadorRepository jugadorRepository;
    private final AsociacionRepository asociacionRepository;
    private final CompeticionRepository competicionRepository;
    private final RelacionService relacionService;

    public ClubRestController(ClubRepository clubRepository, EntrenadorRepository entrenadorRepository,
            JugadorRepository jugadorRepository, AsociacionRepository asociacionRepository,
            CompeticionRepository competicionRepository, RelacionService relacionService) {
        this.clubRepository = clubRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.jugadorRepository = jugadorRepository;
        this.asociacionRepository = asociacionRepository;
        this.competicionRepository = competicionRepository;
        this.relacionService = relacionService;
    }

    @GetMapping
    public List<Club> listar() {
        return clubRepository.findAll();
    }

    @GetMapping("/{id}")
    public Club buscar(@PathVariable("id") String id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club no encontrado: " + id));
    }

    @PostMapping
    public ResponseEntity<Club> crear(@RequestBody ClubRequest request) {
        Club club = armarClub(null, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(clubRepository.save(club));
    }

    @PutMapping("/{id}")
    public Club actualizar(@PathVariable("id") String id, @RequestBody ClubRequest request) {
        buscar(id);
        return clubRepository.save(armarClub(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String id) {
        buscar(id);
        clubRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------- Endpoints de la relación 1 a N -------------------------

    @GetMapping("/{id}/jugadores")
    public List<Jugador> jugadoresDelClub(@PathVariable("id") String id) {
        return buscar(id).getJugadores();
    }

    @PostMapping("/{id}/jugadores/{jugadorId}")
    public Club agregarJugador(@PathVariable("id") String id, @PathVariable("jugadorId") String jugadorId) {
        Club club = buscar(id);
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jugador no encontrado: " + jugadorId));
        relacionService.clubConJugador(jugadorId, id).ifPresent(otro -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El jugador ya pertenece a " + otro.getNombre() + " (relación 1 a muchos).");
        });
        boolean yaEsta = club.getJugadores().stream().anyMatch(j -> j != null && Objects.equals(j.getId(), jugadorId));
        if (!yaEsta) {
            club.getJugadores().add(jugador);
        }
        return clubRepository.save(club);
    }

    @DeleteMapping("/{id}/jugadores/{jugadorId}")
    public Club quitarJugador(@PathVariable("id") String id, @PathVariable("jugadorId") String jugadorId) {
        Club club = buscar(id);
        club.getJugadores().removeIf(j -> j == null || Objects.equals(j.getId(), jugadorId));
        return clubRepository.save(club);
    }

    // ------------------------------------------------------------------------------------

    /** Convierte el JSON con ids en un Club con sus 4 relaciones, validando todo. */
    private Club armarClub(String id, ClubRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del club es obligatorio.");
        }
        Club club = new Club(request.getNombre());
        club.setId(id);

        // 1 a 1
        if (tieneTexto(request.getEntrenadorId())) {
            club.setEntrenador(entrenadorRepository.findById(request.getEntrenadorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Entrenador no existe: " + request.getEntrenadorId())));
            Optional<Club> otro = relacionService.clubConEntrenador(request.getEntrenadorId(), id);
            if (otro.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ese entrenador ya dirige a " + otro.get().getNombre() + " (relación 1 a 1).");
            }
        }

        // N a 1
        if (tieneTexto(request.getAsociacionId())) {
            club.setAsociacion(asociacionRepository.findById(request.getAsociacionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Asociación no existe: " + request.getAsociacionId())));
        }

        // 1 a N
        List<Jugador> jugadores = new ArrayList<>();
        if (request.getJugadoresIds() != null) {
            for (String jugadorId : request.getJugadoresIds()) {
                Jugador j = jugadorRepository.findById(jugadorId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Jugador no existe: " + jugadorId));
                Optional<Club> otro = relacionService.clubConJugador(jugadorId, id);
                if (otro.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El jugador " + j.getNombre() + " "
                            + j.getApellido() + " ya pertenece a " + otro.get().getNombre() + " (relación 1 a muchos).");
                }
                jugadores.add(j);
            }
        }
        club.setJugadores(jugadores);

        // N a N
        List<Competicion> competiciones = new ArrayList<>();
        if (request.getCompeticionesIds() != null) {
            for (String competicionId : request.getCompeticionesIds()) {
                competiciones.add(competicionRepository.findById(competicionId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Competición no existe: " + competicionId)));
            }
        }
        club.setCompeticiones(competiciones);
        return club;
    }

    private static boolean tieneTexto(String s) {
        return s != null && !s.isBlank();
    }
}
