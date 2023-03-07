package com.app.backend.repository;

import com.app.backend.models.EventClick;
import com.app.backend.models.EventResize;
import com.app.backend.models.Site;
import com.app.backend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventClickRepository extends MongoRepository<EventClick, String> {
    List<EventClick> findAllByUserId(String userId);

    List<EventClick> findAllBySite(Site site);

    int  countByUserId(String userId);
    int countByUserIdAndClientTimeBetween(String userId, LocalDateTime start, LocalDateTime end);
    int countBySiteId(String siteId);

}
