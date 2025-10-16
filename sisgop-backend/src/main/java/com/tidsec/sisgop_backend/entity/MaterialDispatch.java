package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.ContractorReceiptState;
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
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "material_dispatch")
public class MaterialDispatch {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialDispatch;

    @Column(name = "dispatch_code", length = 30, unique = true, nullable = false, updatable = false)
    private String dispatchCode;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DISP_PROJECT"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_materials_request", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DISP_REQUEST"))
    private MaterialRequest materialRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user_dispatched_by", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DISP_USER"))
    private User dispatchedBy;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dispatchDate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dispatchUpdateDate;

    @Column(length = 500)
    private String observation;

    // ---- Confirmación del contratista ----
    @Enumerated(EnumType.STRING)
    @Column(name = "contractor_receipt_state", length = 25, nullable = false)
    private ContractorReceiptState contractorReceiptState = ContractorReceiptState.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_received_by", columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_DISP_RECEIVED_BY"))
    private User contractorReceivedBy;   // quién confirmó la recepción (app del contratista)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "contractor_received_at")
    private LocalDateTime contractorReceivedAt; // cuándo confirmó

    @Column(name = "contractor_observation", length = 500)
    private String contractorObservation; // comentario general del despacho (novedades)
}
