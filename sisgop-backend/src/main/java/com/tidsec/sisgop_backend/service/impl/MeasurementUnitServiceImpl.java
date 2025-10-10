package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.entity.MeasurementUnit;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IMeasurementUnitRepository;
import com.tidsec.sisgop_backend.service.IMeasurementUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeasurementUnitServiceImpl extends GenericServiceImpl<MeasurementUnit, UUID> implements IMeasurementUnitService {

    private final IMeasurementUnitRepository measurementUnitRepository;

    @Override
    protected IGenericRepository<MeasurementUnit, UUID> getRepo() {
        return measurementUnitRepository;
    }


    @Override
    public Page<MeasurementUnit> listPage(Pageable pageable) {
        return measurementUnitRepository.findAll(pageable);
    }
}
