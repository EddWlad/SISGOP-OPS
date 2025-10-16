package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.dto.enums.ContractorReceiptState;
import com.tidsec.sisgop_backend.entity.MaterialDispatch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface IMaterialDispatchRepository extends IGenericRepository<MaterialDispatch, UUID>{
    List<MaterialDispatch> findByMaterialRequest_IdMaterialsRequestAndStatusNot(UUID idMaterialsRequest, Integer status);
    List<MaterialDispatch> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);
    List<MaterialDispatch> findByContractorReceiptStateAndStatusNot(ContractorReceiptState state, Integer status);
    List<MaterialDispatch> findByProject_IdProjectAndContractorReceiptStateInAndStatusNot(
            UUID idProject, List<ContractorReceiptState> states, Integer status
    );
    List<MaterialDispatch> findByContractorReceivedBy_IdUserAndStatusNot(UUID idUser, Integer status);

    List<MaterialDispatch> findByProject_IdProjectInAndContractorReceiptStateAndStatusNot(
            Collection<UUID> projectIds, ContractorReceiptState state, Integer status
    );

    // (Opcional) texto en observación
    @Query("SELECT d FROM MaterialDispatch d " +
            "WHERE d.status <> 0 AND " +
            "(LOWER(d.observation) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "  OR LOWER(d.contractorObservation) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<MaterialDispatch> searchByObservation(String q);
}
