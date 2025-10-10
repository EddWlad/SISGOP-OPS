package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.UserProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IUserProjectService extends IGenericService<UserProject, UUID>{
    List<UserProject> findByProject(UUID idProject) throws Exception;
    List<UserProject> findByUser(UUID idUser) throws Exception;
    UserProject upsert(UserProject entity) throws Exception;
    List<UserProject> upsertBatch(List<UserProject> entities) throws Exception;

    Page<UserProject> listPage(Pageable pageable);
}
