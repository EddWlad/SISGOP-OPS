package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.dto.enums.MaterialDevengationState;
import com.tidsec.sisgop_backend.entity.MaterialDevengation;
import com.tidsec.sisgop_backend.entity.MaterialDevengationItem;

import java.util.List;
import java.util.UUID;
public interface IMaterialDevengationService extends IGenericService<MaterialDevengation, UUID>{
    List<MaterialDevengation> findByProject(UUID idProject) throws Exception;
    List<MaterialDevengation> findByState(MaterialDevengationState state) throws Exception;
    List<MaterialDevengation> findByProjectAndStates(UUID idProject, List<MaterialDevengationState> states) throws Exception;

    List<MaterialDevengationItem> findItems(UUID idDevengation) throws Exception;

    MaterialDevengation createWithItems(MaterialDevengation header, List<MaterialDevengationItem> items) throws Exception;
    MaterialDevengation updateWithItems(UUID idDevengation, MaterialDevengation header, List<MaterialDevengationItem> items) throws Exception;

    @Override
    boolean delete(UUID id) throws Exception;
}
