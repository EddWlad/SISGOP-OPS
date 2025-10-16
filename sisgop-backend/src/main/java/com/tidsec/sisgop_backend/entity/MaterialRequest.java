package com.tidsec.sisgop_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tidsec.sisgop_backend.dto.enums.RequestMaterialStatus;
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
@Table(name = "materials_request")
public class MaterialRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    private UUID idMaterialsRequest;

    @Column(name = "request_code", length = 30, unique = true, nullable = false, updatable = false)
    private String requestCode;

    @Column(nullable = false)
    private Integer status = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_request", nullable = false, length = 20)
    private RequestMaterialStatus statusRequest = RequestMaterialStatus.BORRADOR;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "request_date_created")
    private LocalDateTime requestDateCreated = LocalDateTime.now();

    @Column(length = 500)
    private String requestDescription;

    @Column(length = 500)
    private String requestObservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_project", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MATERIALSREQUEST_PROJECT"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false, columnDefinition = "uuid",
            foreignKey = @ForeignKey(name = "FK_MATERIALSREQUEST_USER"))
    private User user;
}
