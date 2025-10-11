package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.entity.StockMovement;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IStockMovementRepository;
import com.tidsec.sisgop_backend.service.IStockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl extends GenericServiceImpl<StockMovement, UUID>
        implements IStockMovementService{

    private final IStockMovementRepository movementRepository;

    @Override
    protected IGenericRepository<StockMovement, UUID> getRepo() {
        return movementRepository;
    }
    @Override
    public List<StockMovement> findByProject(UUID idProject) throws Exception {
        return movementRepository.findByProject_IdProjectAndStatusNot(idProject, 0);
    }
    @Override
    public List<StockMovement> findByProjectAndMaterial(UUID idProject, UUID idMaterial) throws Exception {
        return movementRepository.findByProject_IdProjectAndMaterial_IdMaterialAndStatusNot(idProject, idMaterial, 0);
    }

    @Override
    public Page<StockMovement> listPage(Pageable pageable) {
        return movementRepository.findAll(pageable);
    }
}
