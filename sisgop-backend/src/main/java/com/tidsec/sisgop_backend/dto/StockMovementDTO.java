package com.tidsec.sisgop_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.MovementStockType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StockMovementDTO {
    @EqualsAndHashCode.Include
    private UUID idStockMovement;

    private Integer status = 1;

    private ProjectDTO project;

    private MaterialDTO material;

    private MovementStockType movementType;

    private BigDecimal quantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime movementDate;

    private String referenceType;

    private UUID referenceId;

    private String observation;
}
