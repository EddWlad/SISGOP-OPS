package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.entity.Company;
import com.tidsec.sisgop_backend.entity.MeasurementUnit;
import com.tidsec.sisgop_backend.repository.ICompanyRepository;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.service.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends GenericServiceImpl<Company, UUID> implements ICompanyService {

    private final ICompanyRepository companyRepository;
    @Override
    protected IGenericRepository<Company, UUID> getRepo() {
        return companyRepository;
    }

    @Override
    public Page<Company> listPage(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

}
