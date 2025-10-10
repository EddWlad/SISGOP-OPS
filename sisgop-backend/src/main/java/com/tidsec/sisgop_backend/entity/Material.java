package com.tidsec.sisgop_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name= "material")
public class Material {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterial;

    @Column(nullable = false)
    @Size(min = 3, max = 200)
    private String materialName;

    @Column(nullable = false)
    @Size(min = 3, max = 300)
    private String materialDescription;

    @ManyToOne
    @JoinColumn(name = "id_measurement_unit", foreignKey = @ForeignKey(name = "FK_MATERIAL_UNIT"))
    private MeasurementUnit measurementUnit;

    private List<String> materialImages;

    @Column(nullable = false)
    private Integer status = 1;
}
