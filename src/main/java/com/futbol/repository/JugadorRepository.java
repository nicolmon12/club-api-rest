package com.futbol.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbol.model.Jugador;

public interface JugadorRepository extends MongoRepository<Jugador, String> {
}
