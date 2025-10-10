package com.tidsec.sisgop_backend.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompanyDTO {
    @EqualsAndHashCode.Include
    private UUID idCompany;
    private String companyRuc;
    private String companyName;
    private String companyAddress;
    private String companyLogo;
    private Integer status = 1;
}
