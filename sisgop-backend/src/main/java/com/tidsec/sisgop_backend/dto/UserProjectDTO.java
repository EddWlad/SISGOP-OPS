package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.dto.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProjectDTO {
    @EqualsAndHashCode.Include
    private UUID idUserProject;

    @NotNull
    private ProjectDTO project;

    @NotNull
    private UserDTO user;

    @NotNull
    private ProjectRole projectRole;

    private Integer status = 1;
}
