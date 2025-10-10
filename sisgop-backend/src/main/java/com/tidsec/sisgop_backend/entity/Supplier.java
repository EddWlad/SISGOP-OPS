package com.tidsec.sisgop_backend.entity;

import jakarta.persistence.*;
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
@Table(name= "supplier")
public class Supplier {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idSupplier;

    @Column(nullable = false, unique = true)
    private String supplierName;

    @Column(nullable = false, unique = true)
    private String supplierRuc;

    @Column(nullable = false, unique = true)
    private String supplierEmail;

    @Column(nullable = false, unique = true)
    private String supplierPhone;

    @Column(nullable = false, unique = true)
    private String supplierAddress;

    @Column(nullable = false)
    private Integer status = 1;
}
