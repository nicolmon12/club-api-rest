package com.futbol.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbol.model.Asociacion;

public interface AsociacionRepository extends MongoRepository<Asociacion, String> {
}
