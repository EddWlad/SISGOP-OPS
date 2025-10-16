package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.entity.Material;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorDispatchConfirmItemDTO {

    @NotNull(message = "material es requerido")
    private Material material;

    private Boolean checked;
    private String observation;
    private BigDecimal quantityReceived;
}
