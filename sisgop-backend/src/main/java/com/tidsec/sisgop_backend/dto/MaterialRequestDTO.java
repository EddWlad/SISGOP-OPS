package com.tidsec.sisgop_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.RequestMaterialStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialRequestDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialsRequest;

    private String requestCode;

    private Integer status = 1;
    private RequestMaterialStatus statusRequest;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreated = LocalDateTime.now();

    private String requestDescription;
    private String requestObservation;

    private ProjectDTO project;
    private UserDTO user;

    private List<DetailRequestDTO> items;
}
