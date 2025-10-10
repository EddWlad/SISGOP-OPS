package com.tidsec.sisgop_backend.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MeasurementUnitDTO {
    @EqualsAndHashCode.Include
    private UUID idMeasurementUnit;
    private String unitMeasurementName;
    private Integer status = 1;
}
