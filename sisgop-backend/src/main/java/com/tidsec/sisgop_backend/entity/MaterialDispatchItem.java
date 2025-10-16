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
@Table(name = "material_dispatch_item")
public class MaterialDispatchItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialDispatchItem;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material_dispatch", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DISPITEM_DISP"))
    private MaterialDispatch materialDispatch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DISPITEM_MATERIAL"))
    private Material material;

    @Column(name = "quantity_dispatched", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityDispatched;

    @Column(length = 500)
    private String observation;

    @Column(name = "contractor_checked")
    private Boolean contractorChecked; // check de verificación por ítem (UI móvil)

    @Column(name = "contractor_item_observation", length = 500)
    private String contractorObservation; // nota por ítem si hubo novedad

    // Opcional, si quieres registrar cantidades realmente recibidas:
    @Column(name = "quantity_received", precision = 14, scale = 4)
    private java.math.BigDecimal quantityReceived;
}
