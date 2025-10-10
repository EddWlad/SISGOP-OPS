package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.MeasurementUnit;
import com.tidsec.sisgop_backend.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IMeasurementUnitService extends IGenericService<MeasurementUnit, UUID> {
    Page<MeasurementUnit> listPage(Pageable pageable);
}
