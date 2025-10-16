package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.MaterialDevengationItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IMaterialDevengationItemRepository extends IGenericRepository<MaterialDevengationItem, UUID>{
    // Ítems por cabecera (activos)
    List<MaterialDevengationItem> findByMaterialDevengation_IdMaterialDevengationAndStatusNot(UUID idMD, Integer status);

    // Ítem único por (cabecera, material)
    Optional<MaterialDevengationItem> findByMaterialDevengation_IdMaterialDevengationAndMaterial_IdMaterialAndStatusNot(
            UUID idMD, UUID idMaterial, Integer status
    );
}
