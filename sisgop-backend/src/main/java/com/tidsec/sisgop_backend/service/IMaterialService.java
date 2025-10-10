package com.tidsec.sisgop_backend.service;


import com.tidsec.sisgop_backend.entity.Company;
import com.tidsec.sisgop_backend.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IMaterialService extends IGenericService<Material, UUID> {
    Page<Material> listPage(Pageable pageable);
}
