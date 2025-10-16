package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.dto.enums.TransferReceiptState;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialTransferReceiptConfirmDTO {
    @NotNull(message = "receiptState es requerido")
    private TransferReceiptState receiptState;

    private String observation;

    private List<MaterialTransferReceiptConfirmItemDTO> items;
}
