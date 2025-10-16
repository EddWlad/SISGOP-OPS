package com.tidsec.sisgop_backend.dto;

import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialDispatchItemDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialDispatchItem;

    private Integer status = 1;

    private MaterialDispatchDTO materialDispatch; // opcional en request

    private MaterialDTO material;

    private BigDecimal quantityDispatched;

    private String observation;

    private Boolean contractorChecked;
    private String contractorObservation;
    @Digits(integer = 10, fraction = 4)
    private BigDecimal quantityReceived;
}
