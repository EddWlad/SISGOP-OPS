package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.MaterialDispatch;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IMaterialDispatchRepository extends IGenericRepository<MaterialDispatch, UUID>{
    List<MaterialDispatch> findByMaterialRequest_IdMaterialsRequestAndStatusNot(UUID idMaterialsRequest, Integer status);
    List<MaterialDispatch> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);
}
