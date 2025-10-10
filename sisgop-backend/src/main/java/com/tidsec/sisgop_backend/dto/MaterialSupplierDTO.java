package com.tidsec.sisgop_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialSupplierDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialSupplier;

    @NotNull
    private MaterialDTO material;

    @NotNull
    private SupplierDTO supplier;

    @NotNull
    @DecimalMin("0.0001")
    private BigDecimal price;

    @NotNull
    private Integer leadTimeDays;

    private boolean preferred;

    private Integer status = 1;
}
