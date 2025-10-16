package com.tidsec.sisgop_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.ContractorConsumptionState;
import com.tidsec.sisgop_backend.entity.Project;
import com.tidsec.sisgop_backend.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContractorConsumptionDTO {

    @EqualsAndHashCode.Include
    private UUID idContractorConsumption;

    private Integer status = 1;

    @NotNull(message = "project es requerido")
    private Project project;

    @NotNull(message = "reportedBy es requerido")
    private User reportedBy;

    @NotNull(message = "consumptionState es requerido")
    private ContractorConsumptionState consumptionState = ContractorConsumptionState.BORRADOR;

    @Size(max = 500)
    private String observation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime consumptionDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime consumptionUpdateDate;

    private List<ContractorConsumptionItemDTO> items;
}
