package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.dto.enums.TransferState;
import com.tidsec.sisgop_backend.entity.MaterialTransfer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface IMaterialTransferRepository extends IGenericRepository<MaterialTransfer, UUID>{
    // Búsquedas por código (único y visible)
    Optional<MaterialTransfer> findByTransferCode(String transferCode);
    boolean existsByTransferCode(String transferCode);

    // Por proyecto ORIGEN
    List<MaterialTransfer> findBySourceProject_IdProjectAndStatusNot(UUID idSourceProject, Integer status);

    // Por proyecto DESTINO
    List<MaterialTransfer> findByTargetProject_IdProjectAndStatusNot(UUID idTargetProject, Integer status);

    // Por estado
    List<MaterialTransfer> findByTransferStateAndStatusNot(TransferState state, Integer status);

    // Por ORIGEN + estados (útil para bandejas)
    List<MaterialTransfer> findBySourceProject_IdProjectAndTransferStateInAndStatusNot(
            UUID idSourceProject, List<TransferState> states, Integer status
    );

    // Por DESTINO + estados (útil para bandejas)
    List<MaterialTransfer> findByTargetProject_IdProjectAndTransferStateInAndStatusNot(
            UUID idTargetProject, List<TransferState> states, Integer status
    );
}
