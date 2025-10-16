package com.tidsec.sisgop_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialTransferExecuteDTO {
    @NotEmpty(message = "items es requerido")
    private List<MaterialTransferExecuteItemDTO> items;

    private String observation; // opcional: observación de ejecución
}
