package com.futbol.config;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.futbol.model.Asociacion;
import com.futbol.model.Club;
import com.futbol.model.Competicion;
import com.futbol.model.Entrenador;
import com.futbol.model.Jugador;
import com.futbol.repository.AsociacionRepository;
import com.futbol.repository.ClubRepository;
import com.futbol.repository.CompeticionRepository;
import com.futbol.repository.EntrenadorRepository;
import com.futbol.repository.JugadorRepository;

/**
 * Carga datos de ejemplo la primera vez (si la colección "clubes" está vacía).
 * Se desactiva poniendo app.datos-demo=false en application.properties.
 *
 * OJO con el orden: primero se guardan las entidades "hijas"
 * (asociación, entrenador, jugadores, competiciones) y AL FINAL el Club (clase maestra),
 * porque el club necesita los _id de los documentos que referencia.
 */
@Component
@ConditionalOnProperty(name = "app.datos-demo", havingValue = "true")
public class DatosDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosDemo.class);

    private final AsociacionRepository asociacionRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final JugadorRepository jugadorRepository;
    private final CompeticionRepository competicionRepository;
    private final ClubRepository clubRepository;

    public DatosDemo(AsociacionRepository asociacionRepository, EntrenadorRepository entrenadorRepository,
            JugadorRepository jugadorRepository, CompeticionRepository competicionRepository,
            ClubRepository clubRepository) {
        this.asociacionRepository = asociacionRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.jugadorRepository = jugadorRepository;
        this.competicionRepository = competicionRepository;
        this.clubRepository = clubRepository;
    }

    @Override
    public void run(String... args) {
        if (clubRepository.count() > 0) {
            log.info("Ya hay clubes en MongoDB Atlas, no se cargan datos de ejemplo.");
            return;
        }
        log.info("Cargando datos de ejemplo en MongoDB Atlas...");

        // 1) Hijas
        Asociacion fcf = asociacionRepository.save(
                new Asociacion("FCF - Federación Colombiana de Fútbol", "Colombia", "Carlos Pérez"));

        Entrenador ent1 = entrenadorRepository.save(new Entrenador("Andrés", "Gómez", 45, "Colombiana"));
        Entrenador ent2 = entrenadorRepository.save(new Entrenador("Martín", "Suárez", 52, "Argentina"));

        Jugador j1 = jugadorRepository.save(new Jugador("Juan", "Rodríguez", 1, "Portero"));
        Jugador j2 = jugadorRepository.save(new Jugador("Luis", "Martínez", 10, "Volante"));
        Jugador j3 = jugadorRepository.save(new Jugador("Pedro", "Ramírez", 9, "Delantero"));
        Jugador j4 = jugadorRepository.save(new Jugador("Diego", "Torres", 4, "Defensa"));
        Jugador j5 = jugadorRepository.save(new Jugador("Camilo", "Mena", 8, "Mediocampista"));
        Jugador j6 = jugadorRepository.save(new Jugador("Santiago", "Vargas", 11, "Delantero"));
        jugadorRepository.save(new Jugador("Mateo", "Ruiz", 23, "Defensa"));        // libre (sin club)
        asociacionRepository.save(new Asociacion("CONMEBOL", "Paraguay", "Jorge Salinas")); // sin clubes

        Competicion liga = competicionRepository.save(new Competicion("Liga Colombiana 2026-II", 3000000000L,
                LocalDate.of(2026, 7, 15), LocalDate.of(2026, 12, 15)));
        Competicion copa = competicionRepository.save(new Competicion("Copa Colombia 2026", 1500000000L,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 11, 30)));
        Competicion libertadores = competicionRepository.save(new Competicion("Copa Libertadores 2026",
                23000000000L, LocalDate.of(2026, 2, 3), LocalDate.of(2026, 11, 28)));

        // 2) Clase maestra con sus relaciones
        Club millonarios = new Club("Millonarios");
        millonarios.setEntrenador(ent1);                        // 1 a 1
        millonarios.setJugadores(List.of(j1, j2, j5));              // 1 a N
        millonarios.setAsociacion(fcf);                         // N a 1
        millonarios.setCompeticiones(List.of(liga, copa));      // N a N
        clubRepository.save(millonarios);

        Club santaFe = new Club("Santa Fe");
        santaFe.setEntrenador(ent2);
        santaFe.setJugadores(List.of(j3, j4, j6));
        santaFe.setAsociacion(fcf);
        santaFe.setCompeticiones(List.of(liga, copa, libertadores));
        clubRepository.save(santaFe);

        log.info("Datos de ejemplo cargados: 2 clubes, 2 entrenadores, 7 jugadores, 2 asociaciones, 3 competiciones.");
    }
}
