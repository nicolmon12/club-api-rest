package com.futbol.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbol.model.Entrenador;

public interface EntrenadorRepository extends MongoRepository<Entrenador, String> {
}
