package com.futbol.controller;

import java.util.List;

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

import com.futbol.model.Jugador;
import com.futbol.repository.JugadorRepository;
import com.futbol.service.RelacionService;

/**
 * API REST de jugadores. Responde JSON (no páginas HTML).
 *
 *  GET    /api/jugadores        -> listar
 *  GET    /api/jugadores/{id}   -> buscar uno
 *  POST   /api/jugadores        -> crear   (body JSON)
 *  PUT    /api/jugadores/{id}   -> actualizar (body JSON)
 *  DELETE /api/jugadores/{id}   -> eliminar
 */
@RestController
@RequestMapping("/api/jugadores")
public class JugadorRestController {

    private final JugadorRepository repository;
    private final RelacionService relacionService;

    public JugadorRestController(JugadorRepository repository, RelacionService relacionService) {
        this.repository = repository;
        this.relacionService = relacionService;
    }

    @GetMapping
    public List<Jugador> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Jugador buscar(@PathVariable("id") String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jugador no encontrado: " + id));
    }

    @PostMapping
    public ResponseEntity<Jugador> crear(@RequestBody Jugador jugador) {
        jugador.setId(null); // Mongo genera el _id
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(jugador));
    }

    @PutMapping("/{id}")
    public Jugador actualizar(@PathVariable("id") String id, @RequestBody Jugador jugador) {
        buscar(id); // 404 si no existe
        jugador.setId(id);
        return repository.save(jugador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String id) {
        buscar(id);
        if (relacionService.clubConJugador(id, null).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: un club lo está referenciando (restricción tipo foreign key).");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
