package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IProjectService extends IGenericService<Project, UUID>{
    Page<Project> listPage(Pageable pageable);
}
