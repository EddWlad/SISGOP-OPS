package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.ContractorConsumption;
import com.tidsec.sisgop_backend.dto.enums.ContractorConsumptionState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IContractorConsumptionRepository extends IGenericRepository<ContractorConsumption, UUID>{
    // Por proyecto (activos)
    List<ContractorConsumption> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);

    // Por estado (activos)
    List<ContractorConsumption> findByConsumptionStateAndStatusNot(ContractorConsumptionState state, Integer status);

    // Por proyecto + estados (activos)
    List<ContractorConsumption> findByProject_IdProjectAndConsumptionStateInAndStatusNot(
            UUID idProject, List<ContractorConsumptionState> states, Integer status
    );

    // (Opcional) Por usuario que reportó
    List<ContractorConsumption> findByReportedBy_IdUserAndStatusNot(UUID idUser, Integer status);

    // (Opcional) Búsqueda rápida por texto en observación
    @Query("SELECT c FROM ContractorConsumption c " +
            "WHERE c.status <> 0 AND " +
            "(LOWER(c.observation) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<ContractorConsumption> searchByObservation(String q);
}
