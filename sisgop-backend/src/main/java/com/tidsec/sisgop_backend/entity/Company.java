package com.tidsec.sisgop_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name= "company")
public class Company {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idCompany;

    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 50)
    private String companyRuc;

    @Column(nullable = false)
    @Size(min = 3, max = 50)
    private String companyName;

    @Column(nullable = false)
    @Size(min = 3, max = 50)
    private String companyAddress;

    private String companyLogo;

    @Column(nullable = false)
    private Integer status = 1;
}
