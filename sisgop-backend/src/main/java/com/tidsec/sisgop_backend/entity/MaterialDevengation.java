package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.MaterialDevengationState;
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
@Table(name = "material_devengation")
public class MaterialDevengation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialDevengation;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MD_PROJECT"))
    private Project project;

    // Residente que solicita devengar (tras verificación)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "requested_by", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MD_REQUESTED_BY"))
    private User approvedBy;

    // Usuario de Adquisiciones que "postea" (afecta stock)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "posted_by", columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MD_POSTED_BY"))
    private User postedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "devengation_state", length = 20, nullable = false)
    private MaterialDevengationState devengationState = MaterialDevengationState.PENDIENTE;

    @Column(name = "source_type", length = 30)
    private String sourceType;

    @Column(name = "source_id", columnDefinition = "uuid")
    private UUID sourceId;

    @Column(length = 500)
    private String observation;

    // Igual que tu patrón: timestamps del proceso
    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime devengationDate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime devengationUpdateDate;
}
