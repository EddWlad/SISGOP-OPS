package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.TransferReceiptState;
import com.tidsec.sisgop_backend.dto.enums.TransferState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "material_transfer")
public class MaterialTransfer {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialTransfer;

    @Column(nullable = false)
    private Integer status = 1; // soft-delete

    // Código visible tipo TRF-2025-0001 (se generará en service)
    @Column(name = "transfer_code", length = 30, unique = true)
    private String transferCode;

    // Proyecto ORIGEN
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_source_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_TRF_SOURCE_PROJECT"))
    private Project sourceProject;

    // Proyecto DESTINO
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_target_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_TRF_TARGET_PROJECT"))
    private Project targetProject;

    // Residente que autoriza
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_TRF_REQUESTED_BY"))
    private User requestedBy;

    // Usuario que ejecuta (contratista del origen) - se setea al ejecutar
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executed_by", columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_TRF_EXECUTED_BY"))
    private User executedBy;

    // Usuario que confirma recepción en destino (opcional, acuse) - no mueve stock
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by", columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_TRF_RECEIVED_BY"))
    private User receivedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_state", length = 20, nullable = false)
    private TransferState transferState = TransferState.BORRADOR;

    // Estado de acuse en DESTINO (opcional, similar al de despacho)
    @Enumerated(EnumType.STRING)
    @Column(name = "destination_receipt_state", length = 25)
    private TransferReceiptState destinationReceiptState = TransferReceiptState.PENDIENTE;

    @Column(length = 500)
    private String observation;

    // timestamps
    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transferDateCreated;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transferUpdateDate;

    // fechas de ejecución/recepción (set en procesos)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime executionDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime receptionDate;

    // Observación general del acuse en destino (opcional)
    @Column(name = "destination_observation", length = 500)
    private String destinationObservation;
}
