package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idProject;

    @Column(name = "project_code", length = 50, unique = true)
    private String projectCode;

    @Column(nullable = false)
    private Integer status = 1;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime projectDateCreated = LocalDateTime.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime projectDateStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime projectDateEnd;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_company", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_PROJECT_COMPANY"))
    private Company company;

    @Column(nullable = false, length = 150)
    private String projectName;

    @Column(length = 150)
    private String projectLocation;

    @Column(length = 500)
    private String projectObservation;
}
