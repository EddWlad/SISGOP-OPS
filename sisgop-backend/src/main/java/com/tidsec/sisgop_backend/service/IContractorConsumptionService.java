package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.dto.enums.ContractorConsumptionState;
import com.tidsec.sisgop_backend.entity.ContractorConsumption;
import com.tidsec.sisgop_backend.entity.ContractorConsumptionItem;

import java.util.List;
import java.util.UUID;
public interface IContractorConsumptionService extends IGenericService<ContractorConsumption, UUID>{

    List<ContractorConsumption> findByProject(UUID idProject) throws Exception;
    List<ContractorConsumption> findByState(ContractorConsumptionState state) throws Exception;
    List<ContractorConsumption> findByProjectAndStates(UUID idProject, List<ContractorConsumptionState> states) throws Exception;
    List<ContractorConsumption> findByReportedBy(UUID idUser) throws Exception;

    List<ContractorConsumptionItem> findItems(UUID idConsumption) throws Exception;

    ContractorConsumption saveWithItems(ContractorConsumption header, List<ContractorConsumptionItem> items) throws Exception;
    ContractorConsumption updateWithItems(UUID idConsumption, ContractorConsumption header, List<ContractorConsumptionItem> items) throws Exception;

    ContractorConsumption send(UUID idConsumption) throws Exception;
    ContractorConsumption approve(UUID idConsumption, List<ContractorConsumptionItem> approvals) throws Exception;
    ContractorConsumption reject(UUID idConsumption, String reason) throws Exception;
}
