package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.MaterialSupplier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IMaterialSupplierRepository extends IGenericRepository<MaterialSupplier, UUID>{
    List<MaterialSupplier> findByMaterial_IdMaterialAndStatusNot(UUID idMaterial, Integer status);

    List<MaterialSupplier> findBySupplier_IdSupplierAndStatusNot(UUID idSupplier, Integer status);

    Optional<MaterialSupplier> findByMaterial_IdMaterialAndSupplier_IdSupplierAndStatusNot(
            UUID idMaterial, UUID idSupplier, Integer status
    );

    Optional<MaterialSupplier> findByMaterial_IdMaterialAndSupplier_IdSupplier(UUID idMaterial, UUID idSupplier);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE MaterialSupplier ms
              SET ms.preferred = false
            WHERE ms.material.idMaterial = :idMaterial
              AND ms.idMaterialSupplier <> :currentId
              AND ms.status <> 0
           """)
    int unsetOthersPreferred(UUID idMaterial, UUID currentId);
}
