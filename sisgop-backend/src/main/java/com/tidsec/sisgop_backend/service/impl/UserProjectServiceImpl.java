package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.dto.enums.ProjectRole;
import com.tidsec.sisgop_backend.entity.Project;
import com.tidsec.sisgop_backend.entity.Supplier;
import com.tidsec.sisgop_backend.entity.User;
import com.tidsec.sisgop_backend.entity.UserProject;
import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IProjectRepository;
import com.tidsec.sisgop_backend.repository.IUserProjectRepository;
import com.tidsec.sisgop_backend.repository.IUserRepository;
import com.tidsec.sisgop_backend.service.IUserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProjectServiceImpl extends GenericServiceImpl<UserProject, UUID>
        implements IUserProjectService {

    private final IUserProjectRepository userProjectRepository;
    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;

    @Override
    protected IGenericRepository<UserProject, UUID> getRepo() {
        return userProjectRepository;
    }

    @Override
    public List<UserProject> findByProject(UUID idProject) throws Exception {
        return userProjectRepository.findByProject_IdProjectAndStatusNot(idProject, 0);
    }

    @Override
    public List<UserProject> findByUser(UUID idUser) throws Exception {
        return userProjectRepository.findByUser_IdUserAndStatusNot(idUser, 0);
    }

    @Override
    @Transactional
    public UserProject upsert(UserProject entity) throws Exception {
        return upsertCore(entity);
    }

    @Override
    @Transactional
    public List<UserProject> upsertBatch(List<UserProject> entities) throws Exception {
        List<UserProject> out = new ArrayList<>();
        for (UserProject e : entities) {
            out.add(upsertCore(e));
        }
        return out;
    }

    private UserProject upsertCore(UserProject incoming) throws Exception {
        if (incoming == null || incoming.getProject() == null || incoming.getUser() == null || incoming.getProjectRole() == null) {
            throw new IllegalArgumentException("Se requiere project, user y projectRole en UserProject.");
        }

        UUID idProject = incoming.getProject().getIdProject();
        UUID idUser = incoming.getUser().getIdUser();
        ProjectRole role = incoming.getProjectRole();

        if (idProject == null) throw new IllegalArgumentException("idProject es requerido.");
        if (idUser == null) throw new IllegalArgumentException("idUser es requerido.");

        Project project = projectRepository.findById(idProject)
                .orElseThrow(() -> new ModelNotFoundException("Project no existe: " + idProject));
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new ModelNotFoundException("User no existe: " + idUser));

        UserProject entity = userProjectRepository
                .findByProject_IdProjectAndProjectRole(idProject, role)
                .orElseGet(UserProject::new);

        entity.setProject(project);
        entity.setUser(user);
        entity.setProjectRole(role);
        entity.setStatus(incoming.getStatus() != null ? incoming.getStatus() : 1);

        entity = userProjectRepository.save(entity);

        userProjectRepository.deactivateOthersForRole(idProject, role, entity.getIdUserProject());

        return entity;
    }

    @Override
    public Page<UserProject> listPage(Pageable pageable) {
        return userProjectRepository.findAll(pageable);
    }
}
