package com.app.backend.repository;

import com.app.backend.models.EventResize;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventResizeRepository extends MongoRepository<EventResize, String> {
    List<EventResize> findAllByUserId(String userId);
}
