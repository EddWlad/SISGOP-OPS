package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.dto.enums.TransferReceiptState;
import com.tidsec.sisgop_backend.entity.MaterialTransfer;
import com.tidsec.sisgop_backend.entity.MaterialTransferItem;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
public interface IMaterialTransferService extends IGenericService<MaterialTransfer, UUID> {

    List<MaterialTransfer> findBySource(UUID idSourceProject) throws Exception;
    List<MaterialTransfer> findByTarget(UUID idTargetProject) throws Exception;

    List<MaterialTransferItem> findItems(UUID idMaterialTransfer) throws Exception;

    Page<MaterialTransfer> listPage(Pageable pageable) throws Exception;
    MaterialTransfer createWithItems(MaterialTransfer header,
                                     List<MaterialTransferItem> items) throws Exception;
    MaterialTransfer updateWithItems(MaterialTransfer header,
                                     List<MaterialTransferItem> newItems) throws Exception;
    MaterialTransfer authorize(UUID idMaterialTransfer,
                               UUID idUserWhoAuthorizes) throws Exception;
    MaterialTransfer execute(UUID idMaterialTransfer,
                             List<MaterialTransferItem> execItems,
                             UUID idUserWhoExecutes) throws Exception;
    MaterialTransfer confirmReceipt(UUID idMaterialTransfer,
                                    TransferReceiptState receiptState,
                                    String headerObservation,
                                    List<MaterialTransferItem> destAckItems,
                                    UUID idUserWhoReceives) throws Exception;
}
