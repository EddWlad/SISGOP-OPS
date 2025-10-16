package com.tidsec.sisgop_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.TransferReceiptState;
import com.tidsec.sisgop_backend.dto.enums.TransferState;
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
public class MaterialTransferDTO {
    @EqualsAndHashCode.Include
    private UUID idMaterialTransfer;

    private Integer status = 1;

    // Lo genera el backend (servicio de códigos). El front NO lo envía.
    private String transferCode;

    @NotNull(message = "sourceProject es requerido")
    private Project sourceProject;

    @NotNull(message = "targetProject es requerido")
    private Project targetProject;

    @NotNull(message = "requestedBy es requerido")
    private User requestedBy;

    private User executedBy;     // se setea al ejecutar
    private User receivedBy;     // opcional, acuse en destino

    @NotNull(message = "transferState es requerido")
    private TransferState transferState = TransferState.BORRADOR;

    private TransferReceiptState destinationReceiptState = TransferReceiptState.PENDIENTE;

    @Size(max = 500)
    private String observation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transferDateCreated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transferUpdateDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime executionDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime receptionDate;

    @Size(max = 500)
    private String destinationObservation;

    private List<MaterialTransferItemDTO> items;
}
