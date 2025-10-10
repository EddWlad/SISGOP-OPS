package com.tidsec.sisgop_backend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SupplierDTO {
    @EqualsAndHashCode.Include
    private UUID idSupplier;

    @NotBlank
    @NotNull
    @Size(min = 3, max = 50)
    private String supplierName;

    @NotBlank
    @NotNull
    @Size(min = 3, max = 50)
    private String supplierRuc;

    @NotBlank(message = "El correo electrónico no debe estar vacío")
    @Email(message = "Correo electrónico no válido")
    private String supplierEmail;

    @NotBlank
    @NotNull
    @Size(min = 3, max = 50)
    private String supplierPhone;

    @NotBlank
    @NotNull
    @Size(min = 3, max = 100)
    private String supplierAddress;

    private Integer status = 1;
}
