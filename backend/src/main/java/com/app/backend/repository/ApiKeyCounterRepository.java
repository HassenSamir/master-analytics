package com.app.backend.repository;

import com.app.backend.models.ApiKeyCounter;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ApiKeyCounterRepository extends MongoRepository<ApiKeyCounter, String> {
    ApiKeyCounter findByApiKey(String apiKey);
    void deleteByApiKey(String apiKey);
}