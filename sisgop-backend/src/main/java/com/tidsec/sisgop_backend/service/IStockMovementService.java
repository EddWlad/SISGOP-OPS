package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
public interface IStockMovementService extends IGenericService<StockMovement, UUID>{
    List<StockMovement> findByProject(UUID idProject) throws Exception;

    List<StockMovement> findByProjectAndMaterial(UUID idProject, UUID idMaterial) throws Exception;

    Page<StockMovement> listPage(Pageable pageable);
}
