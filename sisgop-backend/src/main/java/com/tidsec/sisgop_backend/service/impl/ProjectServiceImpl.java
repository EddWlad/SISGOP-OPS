package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.entity.Project;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IProjectRepository;
import com.tidsec.sisgop_backend.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends GenericServiceImpl<Project, UUID> implements IProjectService {

    private final IProjectRepository projectRepository;

    @Override
    public Page<Project> listPage(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Override
    protected IGenericRepository<Project, UUID> getRepo() {
        return projectRepository;
    }
}
