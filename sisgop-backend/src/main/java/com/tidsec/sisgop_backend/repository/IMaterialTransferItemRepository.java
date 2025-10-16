package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.MaterialTransferItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface IMaterialTransferItemRepository extends IGenericRepository<MaterialTransferItem, UUID>{
    // Ítems por cabecera (activos)
    List<MaterialTransferItem> findByMaterialTransfer_IdMaterialTransferAndStatusNot(UUID idMaterialTransfer, Integer status);

    // Ítem único por (cabecera, material) (activo)
    Optional<MaterialTransferItem> findByMaterialTransfer_IdMaterialTransferAndMaterial_IdMaterialAndStatusNot(
            UUID idMaterialTransfer, UUID idMaterial, Integer status
    );
}
