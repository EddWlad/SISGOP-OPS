package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.entity.Material;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorConsumptionApprovalDTO {
    @NotNull(message = "material es requerido")
    private Material material;

    @NotNull
    @Digits(integer = 10, fraction = 4)
    private BigDecimal quantityApproved;
}
