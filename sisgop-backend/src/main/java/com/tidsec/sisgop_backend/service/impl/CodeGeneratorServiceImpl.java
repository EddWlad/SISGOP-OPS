package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.service.ICodeGeneratorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CodeGeneratorServiceImpl implements ICodeGeneratorService {

    @PersistenceContext
    private EntityManager em;

    private static final DateTimeFormatter YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public String nextMaterialRequestCode() {
        long seq = ((Number) em.createNativeQuery("SELECT nextval('material_request_code_seq')").getSingleResult()).longValue();
        return format("SLT", seq);
    }

    @Override
    public String nextMaterialDispatchCode() {
        long seq = ((Number) em.createNativeQuery("SELECT nextval('material_dispatch_code_seq')").getSingleResult()).longValue();
        return format("DES", seq);
    }

    private String format(String prefix, long seq) {
        String ym = LocalDate.now().format(YYYYMM);
        return String.format("%s-%s-%05d", prefix, ym, seq);
    }
}
