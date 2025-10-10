package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.entity.Project;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IProjectRepository extends IGenericRepository<Project, UUID>{
}
