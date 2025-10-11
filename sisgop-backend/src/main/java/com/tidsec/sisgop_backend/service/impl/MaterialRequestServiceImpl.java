package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.dto.enums.RequestMaterialStatus;
import com.tidsec.sisgop_backend.entity.*;
import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.*;
import com.tidsec.sisgop_backend.service.IMaterialRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MaterialRequestServiceImpl extends GenericServiceImpl<MaterialRequest, UUID>
        implements IMaterialRequestService {

    private final IMaterialRequestRepository materialRequestRepository;
    private final IDetailRequestRepository detailRequestRepository;
    private final IMaterialRepository materialRepository;
    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;

    @Override
    protected IGenericRepository<MaterialRequest, UUID> getRepo() {
        return materialRequestRepository;
    }
    @Override
    public List<MaterialRequest> findByProject(UUID idProject) throws Exception {
        return materialRequestRepository.findByProject_IdProjectAndStatusNot(idProject, 0);
    }

    @Override
    public List<MaterialRequest> findByUser(UUID idUser) throws Exception {
        return materialRequestRepository.findByUser_IdUserAndStatusNot(idUser, 0);
    }

    @Override
    public List<MaterialRequest> findByStatusRequest(RequestMaterialStatus statusRequest) throws Exception {
        return materialRequestRepository.findByStatusRequestAndStatusNot(statusRequest, 0);
    }

    @Override
    public List<DetailRequest> findDetails(UUID idMaterialsRequest) throws Exception {
        return detailRequestRepository.findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idMaterialsRequest, 0);
    }

    @Override
    @Transactional
    public MaterialRequest saveWithDetails(MaterialRequest header, List<DetailRequest> details) throws Exception {

        UUID idProject = Optional.ofNullable(header.getProject())
                .map(Project::getIdProject)
                .orElseThrow(() -> new IllegalArgumentException("idProject es requerido"));
        UUID idUser = Optional.ofNullable(header.getUser())
                .map(User::getIdUser)
                .orElseThrow(() -> new IllegalArgumentException("idUser es requerido"));

        Project project = projectRepository.findById(idProject)
                .orElseThrow(() -> new ModelNotFoundException("Project no existe: " + idProject));
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new ModelNotFoundException("User no existe: " + idUser));

        header.setProject(project);
        header.setUser(user);
        if (header.getStatusRequest() == null) header.setStatusRequest(RequestMaterialStatus.BORRADOR);
        if (header.getStatus() == null) header.setStatus(1);

        MaterialRequest savedHeader = materialRequestRepository.save(header);
        upsertDetails(savedHeader, details);

        return savedHeader;
    }

    @Override
    @Transactional
    public MaterialRequest updateWithDetails(MaterialRequest header, List<DetailRequest> details) throws Exception {

        UUID idHeader = Optional.ofNullable(header.getIdMaterialsRequest())
                .orElseThrow(() -> new IllegalArgumentException("idMaterialsRequest es requerido"));

        MaterialRequest current = materialRequestRepository.findById(idHeader)
                .orElseThrow(() -> new ModelNotFoundException("MaterialsRequest no existe: " + idHeader));

        if (current.getStatusRequest() != RequestMaterialStatus.BORRADOR) {
            throw new IllegalStateException("Solo se puede editar cuando la solicitud está en BORRADOR");
        }

        if (header.getRequestObservation() != null) current.setRequestObservation(header.getRequestObservation());
        if (header.getStatus() != null) current.setStatus(header.getStatus());
        if (header.getStatusRequest() != null) current.setStatusRequest(header.getStatusRequest());

        MaterialRequest savedHeader = materialRequestRepository.save(current);
        upsertDetails(savedHeader, details);

        return savedHeader;
    }

    private void upsertDetails(MaterialRequest savedHeader, List<DetailRequest> details) throws Exception {
        if (details == null) return;

        Map<UUID, DetailRequest> byMaterial = new LinkedHashMap<>();

        for (DetailRequest d : details) {
            if (d == null || d.getMaterial() == null || d.getMaterial().getIdMaterial() == null) {
                throw new IllegalArgumentException("Cada detalle debe incluir idMaterial");
            }
            UUID idMaterial = d.getMaterial().getIdMaterial();
            BigDecimal qty = d.getQuantityRequested();
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("quantityRequested debe ser > 0");
            }

            DetailRequest existing = byMaterial.get(idMaterial);
            if (existing == null) {
                byMaterial.put(idMaterial, d);
            } else {
                existing.setQuantityRequested(existing.getQuantityRequested().add(qty));
                String obs = (existing.getObservation() == null ? "" : existing.getObservation() + " | ");
                obs += (d.getObservation() == null ? "" : d.getObservation());
                existing.setObservation(obs.isBlank() ? null : obs);
            }
        }

        for (DetailRequest merged : byMaterial.values()) {
            UUID idMaterial = merged.getMaterial().getIdMaterial();

            Material material = materialRepository.findById(idMaterial)
                    .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMaterial));

            Optional<DetailRequest> opt = detailRequestRepository
                    .findByMaterialsRequest_IdMaterialsRequestAndMaterial_IdMaterial(
                            savedHeader.getIdMaterialsRequest(), idMaterial);

            DetailRequest line = opt.orElseGet(DetailRequest::new);
            line.setMaterialsRequest(savedHeader);
            line.setMaterial(material);
            line.setQuantityRequested(merged.getQuantityRequested());
            line.setObservation(merged.getObservation());
            line.setStatus(merged.getStatus() != null ? merged.getStatus() : 1); // reactivar si estaba en 0

            detailRequestRepository.save(line);
        }
    }

    @Override
    @Transactional
    public MaterialRequest send(UUID idMaterialsRequest) throws Exception {
        MaterialRequest mr = materialRequestRepository.findById(idMaterialsRequest)
                .orElseThrow(() -> new ModelNotFoundException("MaterialsRequest no existe: " + idMaterialsRequest));

        // ÚNICO bloqueo por estado: RECHAZADA no se puede enviar
        if (mr.getStatusRequest() == RequestMaterialStatus.RECHAZADA) {
            throw new IllegalStateException("La solicitud no permite 'send' en estado RECHAZADA.");
        }

        // Traer detalles activos
        List<DetailRequest> items = detailRequestRepository
                .findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idMaterialsRequest, 0);
        if (items.isEmpty()) {
            throw new IllegalStateException("No se puede 'send' sin detalles.");
        }

        boolean anyDispatched = false;
        boolean allComplete = true;

        for (DetailRequest d : items) {
            var rq = d.getQuantityRequested();
            var dp = d.getQuantityDispatched() == null ? java.math.BigDecimal.ZERO : d.getQuantityDispatched();

            if (dp.signum() > 0) anyDispatched = true;
            if (dp.compareTo(rq) < 0) allComplete = false;
        }

        // Si NO hay ningún despacho, no tiene sentido "enviar"
        if (!anyDispatched) {
            throw new IllegalStateException("No se puede 'send' sin despachos registrados.");
        }

        // Fijar estado real según lo ya despachado
        if (allComplete) {
            mr.setStatusRequest(RequestMaterialStatus.COMPLETA);
        } else {
            mr.setStatusRequest(RequestMaterialStatus.PARCIAL);
        }

        return materialRequestRepository.save(mr);
    }

    @Override
    @Transactional
    public MaterialRequest reject(UUID idMaterialsRequest, String reason) throws Exception {
        MaterialRequest mr = materialRequestRepository.findById(idMaterialsRequest)
                .orElseThrow(() -> new ModelNotFoundException("MaterialsRequest no existe: " + idMaterialsRequest));

        var items = detailRequestRepository
                .findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idMaterialsRequest, 0);
        boolean anyDispatched = items.stream()
                .anyMatch(d -> d.getQuantityDispatched() != null && d.getQuantityDispatched().signum() > 0);
        if (anyDispatched) {
            throw new IllegalStateException("No se puede rechazar: ya tiene materiales despachados");
        }

        mr.setStatusRequest(RequestMaterialStatus.RECHAZADA);
        if (reason != null && !reason.isBlank()) {
            mr.setRequestObservation(reason);
        }
        return materialRequestRepository.save(mr);
    }

    @Override
    public Page<MaterialRequest> listPage(Pageable pageable) {
        return materialRequestRepository.findAll(pageable);
    }
}
