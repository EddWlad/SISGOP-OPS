package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.dto.enums.RequestMaterialStatus;
import com.tidsec.sisgop_backend.entity.DetailRequest;
import com.tidsec.sisgop_backend.entity.Material;
import com.tidsec.sisgop_backend.entity.MaterialRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IMaterialRequestService extends IGenericService<MaterialRequest, UUID>{
    List<MaterialRequest> findByProject(UUID idProject) throws Exception;
    List<MaterialRequest> findByUser(UUID idUser) throws Exception;
    List<MaterialRequest> findByStatusRequest(RequestMaterialStatus statusRequest) throws Exception;

    List<DetailRequest> findDetails(UUID idMaterialsRequest) throws Exception;

    MaterialRequest saveWithDetails(MaterialRequest header, List<DetailRequest> details) throws Exception;
    MaterialRequest updateWithDetails(MaterialRequest header, List<DetailRequest> details) throws Exception;

    MaterialRequest send(UUID idMaterialsRequest) throws Exception;

    MaterialRequest reject(UUID idMaterialsRequest, String reason) throws Exception;

    Page<MaterialRequest> listPage(Pageable pageable);
}
