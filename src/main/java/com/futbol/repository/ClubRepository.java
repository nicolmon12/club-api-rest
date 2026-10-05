package com.futbol.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbol.model.Club;

public interface ClubRepository extends MongoRepository<Club, String> {
}
