package com.tidsec.sisgop_backend.dto;

import com.tidsec.sisgop_backend.dto.enums.ContractorReceiptState;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorDispatchConfirmDTO {
    @NotNull(message = "receiptState es requerido")
    private ContractorReceiptState receiptState;

    private String observation;
    private List<ContractorDispatchConfirmItemDTO> items;
}
