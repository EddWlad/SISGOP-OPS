package com.tidsec.sisgop_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjectMaterialStockDTO {
    @EqualsAndHashCode.Include
    private UUID idProjectMaterialStock;

    private Integer status = 1;

    private ProjectDTO project;

    private MaterialDTO material;

    private BigDecimal quantityOnHand;
}
