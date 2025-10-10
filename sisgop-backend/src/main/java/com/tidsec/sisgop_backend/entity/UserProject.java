package com.tidsec.sisgop_backend.entity;

import com.tidsec.sisgop_backend.dto.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "user_project",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_project", "project_role"})
)
public class UserProject {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idUserProject;

    @Column(nullable = false)
    private Integer status = 1; // 1 activo, 0 eliminado (soft)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_USERPROJECT_PROJECT"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_USERPROJECT_USER"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role", nullable = false, length = 30)
    private ProjectRole projectRole;
}
