package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.ProjectMaterialStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
public interface IProjectMaterialStockService extends IGenericService<ProjectMaterialStock, UUID>{
    List<ProjectMaterialStock> findByProject(UUID idProject) throws Exception;

    Page<ProjectMaterialStock> listPage(Pageable pageable);
}
