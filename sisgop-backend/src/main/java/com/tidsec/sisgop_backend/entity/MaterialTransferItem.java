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
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "material_transfer_item",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_material_transfer", "id_material"})
        }
)
public class MaterialTransferItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialTransferItem;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material_transfer", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_TRFI_TRANSFER"))
    private MaterialTransfer materialTransfer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_TRFI_MATERIAL"))
    private Material material;

    @Column(name = "quantity_requested", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityRequested;

    // Se llena al ejecutar (<= requested). 0 por defecto para evitar nulls.
    @Column(name = "quantity_transferred", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityTransferred = BigDecimal.ZERO;

    @Column(length = 500)
    private String observation;

    // ===== Campos de acuse en DESTINO (opcional) =====
    // No mueven stock; solo trazabilidad del recibidor (como hicimos en despacho)
    @Column(name = "dest_checked")
    private Boolean destinationChecked;

    @Column(name = "dest_observation", length = 500)
    private String destinationObservation;

    @Column(name = "quantity_received_dest", precision = 14, scale = 4)
    private BigDecimal quantityReceivedDestination;
}
