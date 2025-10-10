package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.entity.MeasurementUnit;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterial;
    private String materialName;
    private String materialDescription;
    private MeasurementUnit measurementUnit;
    private Integer status = 1;
    private List<String> materialImages;
}
