package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends IGenericRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
