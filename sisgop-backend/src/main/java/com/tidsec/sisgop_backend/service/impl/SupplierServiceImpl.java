package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.entity.Role;
import com.tidsec.sisgop_backend.entity.Supplier;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.ISupplierRepository;
import com.tidsec.sisgop_backend.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends GenericServiceImpl<Supplier, UUID> implements ISupplierService {

    private final ISupplierRepository supplierRepository;
    @Override
    protected IGenericRepository<Supplier, UUID> getRepo() {
        return supplierRepository;
    }

    @Override
    public Page<Supplier> listPage(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }
}
