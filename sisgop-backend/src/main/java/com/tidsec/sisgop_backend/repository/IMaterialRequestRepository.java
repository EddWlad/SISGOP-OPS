package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.dto.enums.RequestMaterialStatus;
import com.tidsec.sisgop_backend.entity.MaterialRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IMaterialRequestRepository extends IGenericRepository<MaterialRequest, UUID>{
    List<MaterialRequest> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);
    List<MaterialRequest> findByUser_IdUserAndStatusNot(UUID idUser, Integer status);
    List<MaterialRequest> findByStatusRequestAndStatusNot(RequestMaterialStatus statusRequest, Integer status);
}
