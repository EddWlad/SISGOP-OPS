package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.MaterialDispatchItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IMaterialDispatchItemRepository extends IGenericRepository<MaterialDispatchItem, UUID>{
    List<MaterialDispatchItem> findByMaterialDispatch_IdMaterialDispatchAndStatusNot(UUID idMaterialDispatch, Integer status);
    Optional<MaterialDispatchItem> findByMaterialDispatch_IdMaterialDispatchAndMaterial_IdMaterialAndStatusNot(
            UUID idDispatch, UUID idMaterial, Integer status
    );
}
