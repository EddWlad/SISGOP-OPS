package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.dto.enums.MovementStockType;
import com.tidsec.sisgop_backend.dto.enums.RequestMaterialStatus;
import com.tidsec.sisgop_backend.entity.*;
import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.*;
import com.tidsec.sisgop_backend.service.IMaterialDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MaterialDispatchServiceImpl extends GenericServiceImpl<MaterialDispatch, UUID>
        implements IMaterialDispatchService {
    private final IMaterialDispatchRepository dispatchRepository;
    private final IMaterialDispatchItemRepository dispatchItemRepository;
    private final IProjectMaterialStockRepository stockRepository;
    private final IStockMovementRepository movementRepository;

    private final IMaterialRequestRepository requestRepository;
    private final IDetailRequestRepository detailRequestRepository;

    private final IProjectRepository projectRepository;
    private final IMaterialRepository materialRepository;
    private final IUserRepository userRepository;

    @Override
    protected IGenericRepository<MaterialDispatch, UUID> getRepo() {
        return dispatchRepository;
    }

    @Override
    public List<MaterialDispatch> findByRequest(UUID idMaterialsRequest) throws Exception {
        return dispatchRepository.findByMaterialRequest_IdMaterialsRequestAndStatusNot(idMaterialsRequest, 0);
    }

    @Override
    public List<MaterialDispatch> findByProject(UUID idProject) throws Exception {
        return dispatchRepository.findByProject_IdProjectAndStatusNot(idProject, 0);
    }

    @Override
    public List<MaterialDispatchItem> findItems(UUID idMaterialDispatch) throws Exception {
        return dispatchItemRepository.findByMaterialDispatch_IdMaterialDispatchAndStatusNot(idMaterialDispatch, 0);
    }

    @Override
    @Transactional
    public MaterialDispatch createWithItems(MaterialDispatch header, List<MaterialDispatchItem> items) throws Exception {
        // ------- Validaciones y FKs cabecera -------
        UUID idProject = Optional.ofNullable(header.getProject())
                .map(Project::getIdProject)
                .orElseThrow(() -> new IllegalArgumentException("idProject es requerido"));

        UUID idRequest = Optional.ofNullable(header.getMaterialRequest())
                .map(MaterialRequest::getIdMaterialsRequest)
                .orElseThrow(() -> new IllegalArgumentException("idMaterialsRequest es requerido"));

        UUID idUser = Optional.ofNullable(header.getDispatchedBy())
                .map(User::getIdUser)
                .orElseThrow(() -> new IllegalArgumentException("idUser (dispatchedBy) es requerido"));

        Project project = projectRepository.findById(idProject)
                .orElseThrow(() -> new ModelNotFoundException("Project no existe: " + idProject));
        MaterialRequest materialRequest = requestRepository.findById(idRequest)
                .orElseThrow(() -> new ModelNotFoundException("MaterialRequest no existe: " + idRequest));
        User dispatchedBy = userRepository.findById(idUser)
                .orElseThrow(() -> new ModelNotFoundException("User no existe: " + idUser));

        // Proyecto de la cabecera debe coincidir con el de la solicitud
        if (!materialRequest.getProject().getIdProject().equals(project.getIdProject())) {
            throw new IllegalStateException("El proyecto del despacho no coincide con el de la solicitud");
        }

        // No permitir despachar si la solicitud está COMPLETA o RECHAZADA
        RequestMaterialStatus st = materialRequest.getStatusRequest();
        if (st == RequestMaterialStatus.COMPLETA || st == RequestMaterialStatus.RECHAZADA) {
            throw new IllegalStateException("La solicitud no permite más despachos por su estado actual: " + st);
        }

        // Set cabecera gestionada
        header.setProject(project);
        header.setMaterialRequest(materialRequest);
        header.setDispatchedBy(dispatchedBy);
        if (header.getStatus() == null) header.setStatus(1);

        // Guardar cabecera
        MaterialDispatch savedHeader = dispatchRepository.save(header);

        if (items == null || items.isEmpty()) {
            // Si quieres permitir cabecera sin items, quita este error
            throw new IllegalArgumentException("El despacho debe contener al menos un item");
        }

        // ------- Pre-cargar detalles de solicitud existentes (para validar y acumular) -------
        Map<UUID, DetailRequest> requestLinesByMaterial = new HashMap<>();
        List<DetailRequest> reqLines = detailRequestRepository
                .findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idRequest, 0);
        for (DetailRequest dr : reqLines) {
            requestLinesByMaterial.put(dr.getMaterial().getIdMaterial(), dr);
        }

        // ------- Consolidar items por material (si en el body vienen repetidos) -------
        Map<UUID, MaterialDispatchItem> consolidated = new LinkedHashMap<>();
        for (MaterialDispatchItem it : items) {
            if (it == null || it.getMaterial() == null || it.getMaterial().getIdMaterial() == null) {
                throw new IllegalArgumentException("Cada item debe incluir idMaterial");
            }
            BigDecimal qty = it.getQuantityDispatched();
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("quantityDispatched debe ser > 0");
            }
            UUID idMat = it.getMaterial().getIdMaterial();
            MaterialDispatchItem acc = consolidated.get(idMat);
            if (acc == null) {
                consolidated.put(idMat, it);
            } else {
                acc.setQuantityDispatched(acc.getQuantityDispatched().add(qty));
                String obs = (acc.getObservation() == null ? "" : acc.getObservation() + " | ")
                        + (it.getObservation() == null ? "" : it.getObservation());
                acc.setObservation(obs.isBlank() ? null : obs);
            }
        }

        // ------- Procesar cada material (guardar item, actualizar stock, kardex, acumulado) -------
        for (MaterialDispatchItem it : consolidated.values()) {
            UUID idMat = it.getMaterial().getIdMaterial();

            // Validar que el material exista en catálogo
            Material material = materialRepository.findById(idMat)
                    .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMat));

            // Validar que exista en la solicitud
            DetailRequest dr = requestLinesByMaterial.get(idMat);
            if (dr == null) {
                throw new IllegalStateException("El material " + idMat + " no existe en la solicitud");
            }

            BigDecimal toDispatch = it.getQuantityDispatched();
            BigDecimal already = dr.getQuantityDispatched() == null ? BigDecimal.ZERO : dr.getQuantityDispatched();
            BigDecimal requested = dr.getQuantityRequested();

            // No exceder lo pendiente
            if (already.add(toDispatch).compareTo(requested) > 0) {
                throw new IllegalStateException("Cantidad a despachar excede lo solicitado para material " + idMat);
            }

            // Guardar item de despacho
            MaterialDispatchItem line = new MaterialDispatchItem();
            line.setMaterialDispatch(savedHeader);
            line.setMaterial(material);
            line.setQuantityDispatched(toDispatch);
            line.setObservation(it.getObservation());
            line.setStatus(1);
            line = dispatchItemRepository.save(line);

            // Upsert stock por (proyecto, material)
            ProjectMaterialStock stock = stockRepository
                    .findByProject_IdProjectAndMaterial_IdMaterial(idProject, idMat)
                    .orElseGet(() -> {
                        ProjectMaterialStock s = new ProjectMaterialStock();
                        s.setProject(project);
                        s.setMaterial(material);
                        s.setQuantityOnHand(BigDecimal.ZERO);
                        s.setStatus(1);
                        return s;
                    });
            stock.setQuantityOnHand(stock.getQuantityOnHand().add(toDispatch));
            stockRepository.save(stock);

            // Registrar movimiento (kardex)
            StockMovement mov = new StockMovement();
            mov.setProject(project);
            mov.setMaterial(material);
            mov.setMovementType(MovementStockType.INGRESO_DESPACHO);
            mov.setQuantity(toDispatch);
            mov.setReferenceType("MATERIAL_DISPATCH_ITEM");
            mov.setReferenceId(line.getIdMaterialDispatchItem());
            mov.setObservation("Despacho " + savedHeader.getIdMaterialDispatch());
            mov.setStatus(1);
            movementRepository.save(mov);

            dr.setQuantityDispatched(already.add(toDispatch));
            detailRequestRepository.save(dr);
        }

        List<DetailRequest> updatedLines = detailRequestRepository
                .findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idRequest, 0);

        boolean anyGt0 = false;
        boolean allComplete = true;
        for (DetailRequest d : updatedLines) {
            BigDecimal rq = d.getQuantityRequested();
            BigDecimal dp = d.getQuantityDispatched() == null ? BigDecimal.ZERO : d.getQuantityDispatched();
            if (dp.compareTo(BigDecimal.ZERO) > 0) anyGt0 = true;
            if (dp.compareTo(rq) < 0) allComplete = false;
        }

        if (allComplete) {
            materialRequest.setStatusRequest(RequestMaterialStatus.COMPLETA);
        } else if (anyGt0) {
            materialRequest.setStatusRequest(RequestMaterialStatus.PARCIAL);
        } // si no, queda ENVIADA
        requestRepository.save(materialRequest);

        return savedHeader;
    }

    @Override
    @Transactional
    public MaterialDispatch updateWithItems(MaterialDispatch header, List<MaterialDispatchItem> newItems) throws Exception {
        // ----- Cargar despacho actual -----
        UUID idDisp = Optional.ofNullable(header.getIdMaterialDispatch())
                .orElseThrow(() -> new IllegalArgumentException("idMaterialDispatch es requerido"));

        MaterialDispatch current = dispatchRepository.findById(idDisp)
                .orElseThrow(() -> new ModelNotFoundException("MaterialDispatch no existe: " + idDisp));

        UUID idProject = current.getProject().getIdProject();
        UUID idRequest = current.getMaterialRequest().getIdMaterialsRequest();

        // Ítems actuales (activos)
        List<MaterialDispatchItem> oldItems = dispatchItemRepository
                .findByMaterialDispatch_IdMaterialDispatchAndStatusNot(idDisp, 0);

        // Mapear actuales por material
        Map<UUID, MaterialDispatchItem> oldByMat = new HashMap<>();
        for (MaterialDispatchItem it : oldItems) {
            oldByMat.put(it.getMaterial().getIdMaterial(), it);
        }

        // Consolidar entrantes por material (sumar repetidos)
        Map<UUID, MaterialDispatchItem> newByMat = new LinkedHashMap<>();
        if (newItems != null) {
            for (MaterialDispatchItem it : newItems) {
                if (it == null || it.getMaterial() == null || it.getMaterial().getIdMaterial() == null) {
                    throw new IllegalArgumentException("Cada item debe incluir idMaterial");
                }
                if (it.getQuantityDispatched() == null || it.getQuantityDispatched().signum() <= 0) {
                    throw new IllegalArgumentException("quantityDispatched debe ser > 0");
                }
                UUID idMat = it.getMaterial().getIdMaterial();
                MaterialDispatchItem acc = newByMat.get(idMat);
                if (acc == null) newByMat.put(idMat, it);
                else {
                    acc.setQuantityDispatched(acc.getQuantityDispatched().add(it.getQuantityDispatched()));
                    String obs = (acc.getObservation() == null ? "" : acc.getObservation() + " | ")
                            + (it.getObservation() == null ? "" : it.getObservation());
                    acc.setObservation(obs.isBlank() ? null : obs);
                }
            }
        }

        // Pre-cargar líneas de solicitud
        Map<UUID, DetailRequest> reqLineByMat = new HashMap<>();
        for (DetailRequest dr : detailRequestRepository
                .findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idRequest, 0)) {
            reqLineByMat.put(dr.getMaterial().getIdMaterial(), dr);
        }

        // Unión de materiales (anteriores ∪ nuevos)
        Set<UUID> allMats = new HashSet<>();
        allMats.addAll(oldByMat.keySet());
        allMats.addAll(newByMat.keySet());

        // Procesar cada material → calcular DELTA = nuevo - anterior
        for (UUID idMat : allMats) {
            BigDecimal oldQty = oldByMat.containsKey(idMat)
                    ? oldByMat.get(idMat).getQuantityDispatched()
                    : BigDecimal.ZERO;
            BigDecimal newQty = newByMat.containsKey(idMat)
                    ? newByMat.get(idMat).getQuantityDispatched()
                    : BigDecimal.ZERO;
            BigDecimal delta = newQty.subtract(oldQty); // puede ser positivo, cero o negativo

            if (delta.signum() == 0) continue; // nada que hacer

            // Validaciones de existencia
            Material material = materialRepository.findById(idMat)
                    .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMat));
            DetailRequest dr = reqLineByMat.get(idMat);
            if (dr == null) {
                throw new IllegalStateException("El material " + idMat + " no existe en la solicitud");
            }

            // Validar no exceder lo solicitado (solo cuando delta > 0)
            if (delta.signum() > 0) {
                BigDecimal already = Optional.ofNullable(dr.getQuantityDispatched()).orElse(BigDecimal.ZERO);
                BigDecimal requested = dr.getQuantityRequested();
                if (already.add(delta).compareTo(requested) > 0) {
                    throw new IllegalStateException("Actualización excede lo solicitado para material " + idMat);
                }
            }

            // 1) Ajustar STOCK (ProjectMaterialStock)
            ProjectMaterialStock stock = stockRepository
                    .findByProject_IdProjectAndMaterial_IdMaterial(idProject, idMat)
                    .orElseGet(() -> {
                        ProjectMaterialStock s = new ProjectMaterialStock();
                        s.setProject(current.getProject());
                        s.setMaterial(material);
                        s.setQuantityOnHand(BigDecimal.ZERO);
                        s.setStatus(1);
                        return s;
                    });
            stock.setQuantityOnHand(stock.getQuantityOnHand().add(delta));
            stockRepository.save(stock);

            // 2) Registrar MOVIMIENTO (delta; si delta < 0, quedará negativo)
            StockMovement mov = new StockMovement();
            mov.setProject(current.getProject());
            mov.setMaterial(material);
            mov.setMovementType(MovementStockType.INGRESO_DESPACHO);
            mov.setQuantity(delta);
            mov.setReferenceType("AJUSTE_MATERIAL_DISPATCH");
            mov.setReferenceId(current.getIdMaterialDispatch());
            mov.setObservation("Ajuste del despacho " + current.getIdMaterialDispatch());
            mov.setStatus(1);
            movementRepository.save(mov);

            // 3) Actualizar ACUMULADO en DetailRequest
            BigDecimal already = Optional.ofNullable(dr.getQuantityDispatched()).orElse(BigDecimal.ZERO);
            dr.setQuantityDispatched(already.add(delta));
            detailRequestRepository.save(dr);
        }

        // Persistir los items:
        // - actualizar cantidades de los que siguen
        // - crear nuevos
        // - soft delete de los eliminados
        for (UUID idMat : allMats) {
            MaterialDispatchItem oldLine = oldByMat.get(idMat);
            MaterialDispatchItem newLine = newByMat.get(idMat);

            if (newLine != null && oldLine != null) {
                // actualizar
                oldLine.setQuantityDispatched(newLine.getQuantityDispatched());
                oldLine.setObservation(newLine.getObservation());
                dispatchItemRepository.save(oldLine);
            } else if (newLine != null) {
                // crear
                Material material = materialRepository.findById(idMat)
                        .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMat));
                MaterialDispatchItem created = new MaterialDispatchItem();
                created.setMaterialDispatch(current);
                created.setMaterial(material);
                created.setQuantityDispatched(newLine.getQuantityDispatched());
                created.setObservation(newLine.getObservation());
                created.setStatus(1);
                dispatchItemRepository.save(created);
            } else {
                // eliminado → soft delete
                oldLine.setStatus(0);
                dispatchItemRepository.save(oldLine);
            }
        }

        // Actualizar cabecera (observación, etc. si viene)
        if (header.getObservation() != null) current.setObservation(header.getObservation());
        if (header.getStatus() != null) current.setStatus(header.getStatus());
        dispatchRepository.save(current);

        // Recalcular estado de la solicitud
        List<DetailRequest> lines = detailRequestRepository
                .findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idRequest, 0);
        boolean anyGt0 = lines.stream().anyMatch(d -> d.getQuantityDispatched().signum() > 0);
        boolean allComplete = lines.stream().allMatch(
                d -> d.getQuantityDispatched().compareTo(d.getQuantityRequested()) == 0
        );

        MaterialRequest req = current.getMaterialRequest();
        if (allComplete) req.setStatusRequest(RequestMaterialStatus.COMPLETA);
        else if (anyGt0) req.setStatusRequest(RequestMaterialStatus.PARCIAL);
        else req.setStatusRequest(RequestMaterialStatus.ENVIADA);
        requestRepository.save(req);

        return current;
    }

    @Override
    @Transactional
    public boolean delete(UUID id) throws Exception {
        MaterialDispatch disp = dispatchRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("MaterialDispatch no existe: " + id));

        UUID idProject = disp.getProject().getIdProject();
        UUID idRequest = disp.getMaterialRequest().getIdMaterialsRequest();

        // Ítems activos
        List<MaterialDispatchItem> items = dispatchItemRepository
                .findByMaterialDispatch_IdMaterialDispatchAndStatusNot(id, 0);

        for (MaterialDispatchItem it : items) {
            UUID idMat = it.getMaterial().getIdMaterial();
            BigDecimal qty = it.getQuantityDispatched();

            // 1) Revertir STOCK
            ProjectMaterialStock stock = stockRepository
                    .findByProject_IdProjectAndMaterial_IdMaterial(idProject, idMat)
                    .orElseThrow(() -> new IllegalStateException("No existe stock para material " + idMat));
            stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(qty));
            stockRepository.save(stock);

            // 2) Movimiento compensatorio (negativo)
            StockMovement mov = new StockMovement();
            mov.setProject(disp.getProject());
            mov.setMaterial(it.getMaterial());
            mov.setMovementType(MovementStockType.INGRESO_DESPACHO);
            mov.setQuantity(qty.negate());
            mov.setReferenceType("ANULACION_MATERIAL_DISPATCH_ITEM");
            mov.setReferenceId(it.getIdMaterialDispatchItem());
            mov.setObservation("Anulación despacho " + disp.getIdMaterialDispatch());
            mov.setStatus(1);
            movementRepository.save(mov);

            // 3) Bajar acumulado en DetailRequest
            DetailRequest dr = detailRequestRepository
                    .findByMaterialsRequest_IdMaterialsRequestAndMaterial_IdMaterial(idRequest, idMat)
                    .orElseThrow(() -> new IllegalStateException("Detalle solicitud no encontrado para material " + idMat));
            dr.setQuantityDispatched(dr.getQuantityDispatched().subtract(qty));
            detailRequestRepository.save(dr);

            // 4) Soft delete del item
            it.setStatus(0);
            dispatchItemRepository.save(it);
        }

        // Recalcular estado de la solicitud
        List<DetailRequest> lines = detailRequestRepository
                .findByMaterialsRequest_IdMaterialsRequestAndStatusNot(idRequest, 0);
        boolean anyGt0 = lines.stream().anyMatch(d -> d.getQuantityDispatched().signum() > 0);
        boolean allComplete = lines.stream().allMatch(
                d -> d.getQuantityDispatched().compareTo(d.getQuantityRequested()) == 0
        );

        MaterialRequest req = disp.getMaterialRequest();
        if (allComplete) req.setStatusRequest(RequestMaterialStatus.COMPLETA);
        else if (anyGt0) req.setStatusRequest(RequestMaterialStatus.PARCIAL);
        else req.setStatusRequest(RequestMaterialStatus.ENVIADA);
        requestRepository.save(req);

        disp.setStatus(0);
        dispatchRepository.save(disp);

        return true;
    }

    @Override
    public Page<MaterialDispatch> listPage(Pageable pageable) {
        return dispatchRepository.findAll(pageable);
    }
}
