package com.tidsec.sisgop_backend.service;


import com.tidsec.sisgop_backend.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ISupplierService extends IGenericService<Supplier, UUID>{
    Page<Supplier> listPage(Pageable pageable);
}
