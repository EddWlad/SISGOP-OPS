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
        name = "detail_request",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_materials_request", "id_material"})
        }
)
public class DetailRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idDetailRequest;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_materials_request", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DETAILREQUEST_MATERIALSREQUEST"))
    private MaterialRequest materialsRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DETAILREQUEST_MATERIAL"))
    private Material material;

    @Column(name = "quantity_requested", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityRequested;

    @Column(length = 500)
    private String observation;
}
