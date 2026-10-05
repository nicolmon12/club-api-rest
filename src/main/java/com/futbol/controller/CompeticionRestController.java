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

import com.futbol.model.Competicion;
import com.futbol.repository.CompeticionRepository;
import com.futbol.service.RelacionService;

/**
 * API REST de competiciones. Responde JSON (no páginas HTML).
 *
 *  GET    /api/competiciones        -> listar
 *  GET    /api/competiciones/{id}   -> buscar uno
 *  POST   /api/competiciones        -> crear   (body JSON)
 *  PUT    /api/competiciones/{id}   -> actualizar (body JSON)
 *  DELETE /api/competiciones/{id}   -> eliminar
 */
@RestController
@RequestMapping("/api/competiciones")
public class CompeticionRestController {

    private final CompeticionRepository repository;
    private final RelacionService relacionService;

    public CompeticionRestController(CompeticionRepository repository, RelacionService relacionService) {
        this.repository = repository;
        this.relacionService = relacionService;
    }

    @GetMapping
    public List<Competicion> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Competicion buscar(@PathVariable("id") String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competición no encontrada: " + id));
    }

    @PostMapping
    public ResponseEntity<Competicion> crear(@RequestBody Competicion competicion) {
        competicion.setId(null); // Mongo genera el _id
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(competicion));
    }

    @PutMapping("/{id}")
    public Competicion actualizar(@PathVariable("id") String id, @RequestBody Competicion competicion) {
        buscar(id); // 404 si no existe
        competicion.setId(id);
        return repository.save(competicion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String id) {
        buscar(id);
        if (relacionService.competicionEnUso(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: un club lo está referenciando (restricción tipo foreign key).");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
