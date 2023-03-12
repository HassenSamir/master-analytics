package com.app.backend.repository;

import com.app.backend.models.EventClick;
import com.app.backend.models.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventClickRepository extends MongoRepository<EventClick, String> {

    Page<EventClick> findAllByUserId(String userId, Pageable pageable);

    Page<EventClick> findAllByUserIdOrderByClientTimeDesc(String userId, Pageable pageable);

    Page<EventClick> findAllBySite(Site site, Pageable pageable);

    int  countByUserId(String userId);
    int countByUserIdAndClientTimeBetween(String userId, LocalDateTime start, LocalDateTime end);
    int countBySiteId(String siteId);
    void deleteBySite(Site site);
}
