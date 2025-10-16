package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.entity.Material;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaterialTransferItemDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialTransferItem;

    private Integer status = 1;

    @NotNull(message = "material es requerido")
    private Material material;

    @NotNull @Positive
    @Digits(integer = 10, fraction = 4)
    private BigDecimal quantityRequested;

    // La llena el backend al ejecutar (puede ser 0..=quantityRequested)
    @Digits(integer = 10, fraction = 4)
    private BigDecimal quantityTransferred;

    @Size(max = 500)
    private String observation;

    // ===== Acuse en destino (opcional, no mueve stock) =====
    private Boolean destinationChecked;

    @Size(max = 500)
    private String destinationObservation;

    @Digits(integer = 10, fraction = 4)
    private BigDecimal quantityReceivedDestination;
}
