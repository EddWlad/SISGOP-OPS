package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.dto.enums.ContractorReceiptState;
import com.tidsec.sisgop_backend.entity.MaterialDispatch;
import com.tidsec.sisgop_backend.entity.MaterialDispatchItem;
import com.tidsec.sisgop_backend.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IMaterialDispatchService extends IGenericService<MaterialDispatch, UUID>{
    List<MaterialDispatch> findByRequest(UUID idMaterialsRequest) throws Exception;
    List<MaterialDispatch> findByProject(UUID idProject) throws Exception;
    List<MaterialDispatchItem> findItems(UUID idMaterialDispatch) throws Exception;
    MaterialDispatch createWithItems(MaterialDispatch header, List<MaterialDispatchItem> items) throws Exception;
    MaterialDispatch updateWithItems(MaterialDispatch header, List<MaterialDispatchItem> newItems) throws Exception;
    Page<MaterialDispatch> listPage(Pageable pageable);

    MaterialDispatch confirmReceipt(
            UUID idDispatch,
            ContractorReceiptState state,      // RECIBIDO | RECIBIDO_NOVEDADES
            String headerObservation,          // obs general
            List<MaterialDispatchItem> itemsForAck, // por cada material: contractorChecked, contractorObservation, quantityReceived (opcional)
            UUID idUserWhoConfirms             // usuario que confirma
    ) throws Exception;


}
