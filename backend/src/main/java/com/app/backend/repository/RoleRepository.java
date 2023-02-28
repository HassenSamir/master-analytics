package com.app.backend.repository;

import java.util.Optional;

import com.app.backend.models.ERole;
import com.app.backend.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
