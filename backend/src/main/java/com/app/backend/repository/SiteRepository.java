package com.app.backend.repository;

import com.app.backend.models.Site;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends MongoRepository<Site, String> {
    List<Site> findAllByUserId(String userId);
    Optional<Site> findByName(String name);
    Optional<Site> findById(String id);

    Optional<Site> findByUserIdAndName(String userId, String name);

    Optional<Site> findByApiKey(String apiKey);

    Optional<Site> findByIdAndUserId(String id, String userId);

}