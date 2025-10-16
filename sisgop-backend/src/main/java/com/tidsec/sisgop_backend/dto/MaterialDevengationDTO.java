package com.tidsec.sisgop_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.tidsec.sisgop_backend.dto.enums.MaterialDevengationState;
import com.tidsec.sisgop_backend.entity.Project;
import com.tidsec.sisgop_backend.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialDevengationDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialDevengation;

    private Integer status = 1;

    @NotNull(message = "project es requerido")
    private Project project;

    @NotNull(message = "approvedBy es requerido")
    private User approvedBy;

    @NotNull(message = "postedBy es requerido")
    private User postedBy;

    @NotNull(message = "devengationState es requerido")
    private MaterialDevengationState devengationState;

    private String sourceType;
    private UUID sourceId;

    @Size(max = 500)
    private String observation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime devengationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime devengationUpdateDate;

    private List<MaterialDevengationItemDTO> items;
}
