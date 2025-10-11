package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.ProjectMaterialStock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProjectMaterialStockRepository extends IGenericRepository<ProjectMaterialStock, UUID>{
    Optional<ProjectMaterialStock> findByProject_IdProjectAndMaterial_IdMaterial(UUID idProject, UUID idMaterial);
    List<ProjectMaterialStock> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);
}
