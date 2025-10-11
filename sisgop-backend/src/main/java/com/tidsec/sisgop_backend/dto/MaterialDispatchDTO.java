package com.tidsec.sisgop_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialDispatchDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialDispatch;

    private Integer status = 1;

    private ProjectDTO project;

    private MaterialRequestDTO materialRequest;

    private UserDTO dispatchedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dispatchDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dispatchUpdateDate;

    private String observation;

    private List<MaterialDispatchItemDTO> items;
}
