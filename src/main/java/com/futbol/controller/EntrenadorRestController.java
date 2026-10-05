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

import com.futbol.model.Entrenador;
import com.futbol.repository.EntrenadorRepository;
import com.futbol.service.RelacionService;

/**
 * API REST de entrenadores. Responde JSON (no páginas HTML).
 *
 *  GET    /api/entrenadores        -> listar
 *  GET    /api/entrenadores/{id}   -> buscar uno
 *  POST   /api/entrenadores        -> crear   (body JSON)
 *  PUT    /api/entrenadores/{id}   -> actualizar (body JSON)
 *  DELETE /api/entrenadores/{id}   -> eliminar
 */
@RestController
@RequestMapping("/api/entrenadores")
public class EntrenadorRestController {

    private final EntrenadorRepository repository;
    private final RelacionService relacionService;

    public EntrenadorRestController(EntrenadorRepository repository, RelacionService relacionService) {
        this.repository = repository;
        this.relacionService = relacionService;
    }

    @GetMapping
    public List<Entrenador> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Entrenador buscar(@PathVariable("id") String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrenador no encontrado: " + id));
    }

    @PostMapping
    public ResponseEntity<Entrenador> crear(@RequestBody Entrenador entrenador) {
        entrenador.setId(null); // Mongo genera el _id
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(entrenador));
    }

    @PutMapping("/{id}")
    public Entrenador actualizar(@PathVariable("id") String id, @RequestBody Entrenador entrenador) {
        buscar(id); // 404 si no existe
        entrenador.setId(id);
        return repository.save(entrenador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String id) {
        buscar(id);
        if (relacionService.clubConEntrenador(id, null).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: un club lo está referenciando (restricción tipo foreign key).");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
