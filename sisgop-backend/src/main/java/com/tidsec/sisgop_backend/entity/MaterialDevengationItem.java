package com.tidsec.sisgop_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "material_devengation_item",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_material_devengation", "id_material"})
        }
)
public class MaterialDevengationItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialDevengationItem;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material_devengation", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MDI_MD"))
    private MaterialDevengation materialDevengation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MDI_MATERIAL"))
    private Material material;

    @Column(name = "quantity_consumed", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityConsumed;

    @Column(length = 500)
    private String observation;
}
