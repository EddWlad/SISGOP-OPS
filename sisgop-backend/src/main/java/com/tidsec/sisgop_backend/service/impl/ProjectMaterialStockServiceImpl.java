package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.entity.ProjectMaterialStock;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IProjectMaterialStockRepository;
import com.tidsec.sisgop_backend.service.IProjectMaterialStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectMaterialStockServiceImpl extends GenericServiceImpl<ProjectMaterialStock, UUID>
        implements IProjectMaterialStockService{

    private final IProjectMaterialStockRepository stockRepository;

    @Override
    protected IGenericRepository<ProjectMaterialStock, UUID> getRepo() {
        return stockRepository;
    }

    @Override
    public List<ProjectMaterialStock> findByProject(UUID idProject) throws Exception {
        return stockRepository.findByProject_IdProjectAndStatusNot(idProject, 0);
    }

    @Override
    public Page<ProjectMaterialStock> listPage(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }
}
