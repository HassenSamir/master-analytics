package com.app.backend.repository;

import com.app.backend.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventResizeRepository extends MongoRepository<EventResize, String> {
    Page<EventResize> findAllByUserId(String userId, Pageable pageable);
    Page<EventResize> findAllBySite(Site site, Pageable pageable);
    Page<EventResize> findAllByUserIdOrderByClientTimeDesc(String userId, Pageable pageable);

    int  countByUserId(String userId);
    int countByUserIdAndClientTimeBetween(String userId, LocalDateTime start, LocalDateTime end);
    int countBySiteId(String siteId);
}
