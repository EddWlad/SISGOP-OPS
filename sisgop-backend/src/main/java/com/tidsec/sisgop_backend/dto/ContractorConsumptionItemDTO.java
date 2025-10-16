package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.entity.ContractorConsumption;
import com.tidsec.sisgop_backend.entity.Material;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContractorConsumptionItemDTO {
    @EqualsAndHashCode.Include
    private UUID idContractorConsumptionItem;

    private Integer status = 1;

    @NotNull(message = "material es requerido")
    private Material material;

    private ContractorConsumption contractorConsumption;

    @NotNull @Positive
    @Digits(integer = 10, fraction = 4)
    private BigDecimal quantityUsed;

    @NotNull
    @Digits(integer = 10, fraction = 4)
    private BigDecimal quantityApproved = BigDecimal.ZERO;

    @Size(max = 500)
    private String observation;
}
