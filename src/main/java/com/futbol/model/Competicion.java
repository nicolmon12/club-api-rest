package com.futbol.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Competición (Liga, Copa, Libertadores...).
 * Relación Muchos a Muchos: un Club participa en MUCHAS Competiciones
 * y una Competición tiene MUCHOS Clubes.
 */
@Document(collection = "competiciones")
public class Competicion {

    @Id
    private String id;
    private String nombre;
    private long montoPremio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    public Competicion() {
    }

    public Competicion(String nombre, long montoPremio, LocalDate fechaInicio, LocalDate fechaFin) {
        this.nombre = nombre;
        this.montoPremio = montoPremio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
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

    public long getMontoPremio() {
        return montoPremio;
    }

    public void setMontoPremio(long montoPremio) {
        this.montoPremio = montoPremio;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /** Calculado (no se guarda en MongoDB): Próxima, En curso o Finalizada. */
    public String getEstado() {
        LocalDate hoy = LocalDate.now();
        if (fechaInicio != null && hoy.isBefore(fechaInicio)) {
            return "Próxima";
        }
        if (fechaFin != null && hoy.isAfter(fechaFin)) {
            return "Finalizada";
        }
        return "En curso";
    }

    /** Calculado: porcentaje de avance de la competición (0 a 100). */
    public int getProgreso() {
        if (fechaInicio == null || fechaFin == null || !fechaFin.isAfter(fechaInicio)) {
            return 0;
        }
        long total = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        long pasados = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, LocalDate.now());
        return (int) Math.max(0, Math.min(100, pasados * 100 / total));
    }
}
