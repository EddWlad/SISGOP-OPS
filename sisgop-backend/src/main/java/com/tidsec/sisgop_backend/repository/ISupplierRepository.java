package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.Supplier;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ISupplierRepository extends IGenericRepository<Supplier, UUID>{
}
