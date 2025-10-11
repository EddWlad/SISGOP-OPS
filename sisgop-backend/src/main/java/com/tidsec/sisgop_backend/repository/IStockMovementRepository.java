package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.StockMovement;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IStockMovementRepository extends IGenericRepository<StockMovement, UUID>{
    List<StockMovement> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);
    List<StockMovement> findByProject_IdProjectAndMaterial_IdMaterialAndStatusNot(UUID idProject, UUID idMaterial, Integer status);
}
