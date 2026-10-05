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

import com.futbol.model.Asociacion;
import com.futbol.repository.AsociacionRepository;
import com.futbol.service.RelacionService;

/**
 * API REST de asociaciones. Responde JSON (no páginas HTML).
 *
 *  GET    /api/asociaciones        -> listar
 *  GET    /api/asociaciones/{id}   -> buscar uno
 *  POST   /api/asociaciones        -> crear   (body JSON)
 *  PUT    /api/asociaciones/{id}   -> actualizar (body JSON)
 *  DELETE /api/asociaciones/{id}   -> eliminar
 */
@RestController
@RequestMapping("/api/asociaciones")
public class AsociacionRestController {

    private final AsociacionRepository repository;
    private final RelacionService relacionService;

    public AsociacionRestController(AsociacionRepository repository, RelacionService relacionService) {
        this.repository = repository;
        this.relacionService = relacionService;
    }

    @GetMapping
    public List<Asociacion> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Asociacion buscar(@PathVariable("id") String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asociación no encontrada: " + id));
    }

    @PostMapping
    public ResponseEntity<Asociacion> crear(@RequestBody Asociacion asociacion) {
        asociacion.setId(null); // Mongo genera el _id
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(asociacion));
    }

    @PutMapping("/{id}")
    public Asociacion actualizar(@PathVariable("id") String id, @RequestBody Asociacion asociacion) {
        buscar(id); // 404 si no existe
        asociacion.setId(id);
        return repository.save(asociacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String id) {
        buscar(id);
        if (relacionService.asociacionEnUso(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: un club lo está referenciando (restricción tipo foreign key).");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
