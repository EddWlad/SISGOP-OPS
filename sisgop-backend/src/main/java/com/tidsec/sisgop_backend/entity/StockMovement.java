package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.MovementStockType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "stock_movement")
public class StockMovement {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idStockMovement;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MOV_PROJECT"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MOV_MATERIAL"))
    private Material material;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementStockType movementType = MovementStockType.INGRESO_DESPACHO;

    @Column(name = "quantity", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime movementDate = LocalDateTime.now();

    @Column(name = "reference_type", length = 40)
    private String referenceType;

    @Column(name = "reference_id", columnDefinition = "uuid")
    private UUID referenceId;

    @Column(length = 500)
    private String observation;
}
