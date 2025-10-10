package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.Company;
import com.tidsec.sisgop_backend.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICompanyService extends IGenericService<Company, UUID> {
    Page<Company> listPage(Pageable pageable);
}
