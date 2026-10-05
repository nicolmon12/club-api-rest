package com.futbol.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbol.model.Competicion;

public interface CompeticionRepository extends MongoRepository<Competicion, String> {
}
