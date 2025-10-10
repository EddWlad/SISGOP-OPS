package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name= "material_supplier")
public class MaterialSupplier {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialSupplier;

    @CreationTimestamp
    @Column(name = "date_create", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datUpdate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_material", foreignKey = @ForeignKey(name = "FK_MATERIAL_SUPPLIER"))
    private Material material;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_supplier", foreignKey = @ForeignKey(name = "FK_SUPPLIER_MATERIAL"))
    private Supplier supplier;

    @Column(nullable = false, length = 10)
    private String currency = "USD";

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer leadTimeDays;

    @Column(nullable = false)
    private boolean preferred = false;

    @Column(nullable = false)
    private Integer status = 1;

}
