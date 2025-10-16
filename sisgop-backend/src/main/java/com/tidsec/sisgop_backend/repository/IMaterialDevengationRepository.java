package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.dto.enums.MaterialDevengationState;
import com.tidsec.sisgop_backend.entity.MaterialDevengation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IMaterialDevengationRepository extends IGenericRepository<MaterialDevengation, UUID>{
    // Por proyecto (activos)
    List<MaterialDevengation> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);

    // Por estado (activos)
    List<MaterialDevengation> findByDevengationStateAndStatusNot(MaterialDevengationState state, Integer status);

    // Por proyecto + estados (activos)
    List<MaterialDevengation> findByProject_IdProjectAndDevengationStateInAndStatusNot(
            UUID idProject, List<MaterialDevengationState> states, Integer status
    );

    // (Opcional) Por aprobador o publicador
    List<MaterialDevengation> findByApprovedBy_IdUserAndStatusNot(UUID idUser, Integer status);
    List<MaterialDevengation> findByPostedBy_IdUserAndStatusNot(UUID idUser, Integer status);
}
