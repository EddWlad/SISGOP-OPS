package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.ContractorConsumptionItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IContractorConsumptionItemRepository extends IGenericRepository<ContractorConsumptionItem, UUID>{
    // Ítems por cabecera (activos)
    List<ContractorConsumptionItem> findByContractorConsumption_IdContractorConsumptionAndStatusNot(UUID idCC, Integer status);

    // Ítem único por (cabecera, material)
    Optional<ContractorConsumptionItem> findByContractorConsumption_IdContractorConsumptionAndMaterial_IdMaterialAndStatusNot(
            UUID idCC, UUID idMaterial, Integer status
    );
}
