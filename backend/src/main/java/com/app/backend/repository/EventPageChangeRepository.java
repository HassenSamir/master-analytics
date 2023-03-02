package com.app.backend.repository;

import com.app.backend.models.EventPageChange;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPageChangeRepository extends MongoRepository<EventPageChange, String> {
    List<EventPageChange> findAllByUserId(String userId);
}
