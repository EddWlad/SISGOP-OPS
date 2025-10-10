package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.Company;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ICompanyRepository extends IGenericRepository<Company, UUID> {
}
