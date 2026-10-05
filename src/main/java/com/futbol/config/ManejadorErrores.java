package com.futbol.config;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** Devuelve los errores de la API como JSON sencillo: { "status": 404, "error": "..." } */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejar(ResponseStatusException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("fecha", LocalDateTime.now().toString());
        cuerpo.put("status", ex.getStatusCode().value());
        cuerpo.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(cuerpo);
    }
}
