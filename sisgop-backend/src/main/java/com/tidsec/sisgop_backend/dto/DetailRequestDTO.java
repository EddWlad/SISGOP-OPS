package com.tidsec.sisgop_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DetailRequestDTO {
    @EqualsAndHashCode.Include
    private UUID idDetailRequest;

    private Integer status = 1;
    private MaterialRequestDTO materialsRequest;
    private MaterialDTO material;
    private BigDecimal quantityRequested;
    private String observation;
}
