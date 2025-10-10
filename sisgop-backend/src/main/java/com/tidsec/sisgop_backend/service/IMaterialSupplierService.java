package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.MaterialSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IMaterialSupplierService extends IGenericService<MaterialSupplier, UUID>{
    List<MaterialSupplier> findByMaterial(UUID idMaterial) throws Exception;
    List<MaterialSupplier> findBySupplier(UUID idSupplier) throws Exception;
    MaterialSupplier upsert(MaterialSupplier entity) throws Exception;
    List<MaterialSupplier> upsertBatch(List<MaterialSupplier> entities) throws Exception;

    Page<MaterialSupplier> listPage(Pageable pageable);
}
