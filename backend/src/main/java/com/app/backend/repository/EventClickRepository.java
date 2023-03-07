package com.app.backend.repository;

import com.app.backend.models.EventClick;
import com.app.backend.models.Site;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventClickRepository extends MongoRepository<EventClick, String> {
    List<EventClick> findAllByUserId(String userId);

    List<EventClick> findAllBySite(Site site);

}
