package com.tidsec.sisgop_backend.repository;

import com.tidsec.sisgop_backend.dto.enums.ProjectRole;
import com.tidsec.sisgop_backend.entity.UserProject;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserProjectRepository extends IGenericRepository<UserProject, UUID> {
    List<UserProject> findByProject_IdProjectAndStatusNot(UUID idProject, Integer status);

    List<UserProject> findByUser_IdUserAndStatusNot(UUID idUser, Integer status);

    Optional<UserProject> findByProject_IdProjectAndProjectRoleAndStatusNot(
            UUID idProject,
            ProjectRole projectRole,
            Integer status
    );

    Optional<UserProject> findByProject_IdProjectAndProjectRole(
            UUID idProject,
            ProjectRole projectRole
    );
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE UserProject up
              SET up.status = 0
            WHERE up.project.idProject = :idProject
              AND up.projectRole = :projectRole
              AND up.idUserProject <> :currentId
              AND up.status <> 0
           """)
    int deactivateOthersForRole(UUID idProject, ProjectRole projectRole, UUID currentId);
}
