package com.tidsec.sisgop_backend.entity;

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
@Table(
        name = "contractor_consumption_item",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_contractor_consumption", "id_material"})
        }
)
public class ContractorConsumptionItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idContractorConsumptionItem;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_contractor_consumption", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_CCI_CC"))
    private ContractorConsumption contractorConsumption;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_CCI_MATERIAL"))
    private Material material;

    @Column(name = "quantity_used", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityUsed;

    @Column(name = "quantity_approved", precision = 14, scale = 4)
    private BigDecimal quantityApproved = BigDecimal.ZERO;

    @Column(length = 500)
    private String observation;
}
