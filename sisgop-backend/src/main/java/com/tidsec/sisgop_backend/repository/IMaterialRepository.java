package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.Material;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IMaterialRepository extends IGenericRepository<Material, UUID> {
}
