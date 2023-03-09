package com.app.backend.repository;

import com.app.backend.models.EventPageChange;
import com.app.backend.models.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventPageChangeRepository extends MongoRepository<EventPageChange, String> {
    Page<EventPageChange> findAllByUserId(String userId, Pageable pageable);
    Page<EventPageChange> findAllBySite(Site site, Pageable pageable);
    Page<EventPageChange> findAllByUserIdOrderByClientTimeDesc(String userId, Pageable pageable);

    int  countByUserId(String userId);
    int countByUserIdAndClientTimeBetween(String userId, LocalDateTime start, LocalDateTime end);
    int countBySiteId(String siteId);
}
