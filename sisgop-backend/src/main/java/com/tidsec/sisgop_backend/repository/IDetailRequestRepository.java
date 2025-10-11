package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.DetailRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDetailRequestRepository extends IGenericRepository<DetailRequest, UUID>{
    List<DetailRequest> findByMaterialsRequest_IdMaterialsRequestAndStatusNot(UUID idMaterialsRequest, Integer status);
    Optional<DetailRequest> findByMaterialsRequest_IdMaterialsRequestAndMaterial_IdMaterial(
            UUID idMaterialsRequest,
            UUID idMaterial
    );

}
