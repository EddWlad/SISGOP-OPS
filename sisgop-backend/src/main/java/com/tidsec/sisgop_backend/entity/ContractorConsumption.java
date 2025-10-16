package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.ContractorConsumptionState;
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
@Table( name = "contractor_consumption")
public class ContractorConsumption {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idContractorConsumption;

    @Column(nullable = false)
    private Integer status = 1; // soft-delete

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_CC_PROJECT"))
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reported_by", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_CC_REPORTED_BY"))
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_state", length = 20, nullable = false)
    private ContractorConsumptionState consumptionState = ContractorConsumptionState.BORRADOR;

    @Column(length = 500)
    private String observation;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime consumptionDate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime consumptionUpdateDate;
}
