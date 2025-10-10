package com.tidsec.sisgop_backend.service.impl;


import com.tidsec.sisgop_backend.entity.Company;
import com.tidsec.sisgop_backend.entity.Material;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IMaterialRepository;
import com.tidsec.sisgop_backend.service.IMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends GenericServiceImpl<Material, UUID> implements IMaterialService {

    private final IMaterialRepository materialRepository;

    @Override
    protected IGenericRepository<Material, UUID> getRepo() {
        return materialRepository;
    }

    @Override
    public Page<Material> listPage(Pageable pageable) {
        return materialRepository.findAll(pageable);
    }


}
