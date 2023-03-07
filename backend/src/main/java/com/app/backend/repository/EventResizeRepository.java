package com.app.backend.repository;

import com.app.backend.models.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventResizeRepository extends MongoRepository<EventResize, String> {
    List<EventResize> findAllByUserId(String userId);
    List<EventResize> findAllBySite(Site site);
    int  countByUserId(String userId);
    int countByUserIdAndClientTimeBetween(String userId, LocalDateTime start, LocalDateTime end);
}
