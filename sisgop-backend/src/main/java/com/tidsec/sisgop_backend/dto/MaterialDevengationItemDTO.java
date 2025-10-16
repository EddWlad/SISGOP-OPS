package com.tidsec.sisgop_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialDevengationItemDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialDevengationItem;

    private Integer status = 1;

    private MaterialDevengationDTO materialDevengation;
    private MaterialDTO material;

    private BigDecimal quantityConsumed;
    private String observation;
}
