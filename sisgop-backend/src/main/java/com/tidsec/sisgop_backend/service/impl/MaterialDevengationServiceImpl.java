package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.dto.enums.MaterialDevengationState;
import com.tidsec.sisgop_backend.dto.enums.MovementStockType;
import com.tidsec.sisgop_backend.entity.*;
import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.*;
import com.tidsec.sisgop_backend.service.IMaterialDevengationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MaterialDevengationServiceImpl extends GenericServiceImpl<MaterialDevengation, UUID>
        implements IMaterialDevengationService{

    private final IMaterialDevengationRepository repository;
    private final IMaterialDevengationItemRepository itemRepository;

    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;
    private final IMaterialRepository materialRepository;
    private final IProjectMaterialStockRepository stockRepository;
    private final IStockMovementRepository movementRepository;

    @Override
    protected IGenericRepository<MaterialDevengation, UUID> getRepo() {
        return repository;
    }

    @Override
    public List<MaterialDevengation> findByProject(UUID idProject) {
        return repository.findByProject_IdProjectAndStatusNot(idProject, 0);
    }

    @Override
    public List<MaterialDevengation> findByState(MaterialDevengationState state) {
        return repository.findByDevengationStateAndStatusNot(state, 0);
    }

    @Override
    public List<MaterialDevengation> findByProjectAndStates(UUID idProject, List<MaterialDevengationState> states) {
        return repository.findByProject_IdProjectAndDevengationStateInAndStatusNot(idProject, states, 0);
    }

    @Override
    public List<MaterialDevengationItem> findItems(UUID idDevengation) {
        return itemRepository.findByMaterialDevengation_IdMaterialDevengationAndStatusNot(idDevengation, 0);
    }

    // ========= Helpers de stock/kardex =========
    private ProjectMaterialStock getOrCreateStock(UUID idProject, UUID idMaterial) {
        return stockRepository
                .findByProject_IdProjectAndMaterial_IdMaterial(idProject, idMaterial)
                .filter(s -> s.getStatus() != null && s.getStatus() != 0)
                .orElseGet(() -> {
                    ProjectMaterialStock s = new ProjectMaterialStock();
                    s.setStatus(1);
                    Project p = new Project(); p.setIdProject(idProject); s.setProject(p);
                    Material m = new Material(); m.setIdMaterial(idMaterial); s.setMaterial(m);
                    s.setQuantityOnHand(java.math.BigDecimal.ZERO);
                    return s;
                });
    }

    private void addMovement(UUID idProject, UUID idMaterial, BigDecimal qty, String obs, UUID refId) {
        StockMovement mv = new StockMovement();
        mv.setIdStockMovement(null);
        mv.setStatus(1);
        Project p = new Project(); p.setIdProject(idProject); mv.setProject(p);
        Material m = new Material(); m.setIdMaterial(idMaterial); mv.setMaterial(m);
        mv.setMovementType(MovementStockType.EGRESO_DEVENGACION);
        mv.setQuantity(qty); // NEGATIVO para egreso (consistente con tu ingreso positivo)
        mv.setReferenceId(refId);
        mv.setObservation(obs);
        movementRepository.save(mv);
    }

    // ========= Core =========

    @Override
    @Transactional
    public MaterialDevengation createWithItems(MaterialDevengation header, List<MaterialDevengationItem> items) throws Exception {
        UUID idProject = Optional.ofNullable(header.getProject()).map(Project::getIdProject)
                .orElseThrow(() -> new IllegalArgumentException("project es requerido"));
        UUID idApprovedBy = Optional.ofNullable(header.getApprovedBy()).map(User::getIdUser)
                .orElseThrow(() -> new IllegalArgumentException("approvedBy es requerido"));
        UUID idPostedBy = Optional.ofNullable(header.getPostedBy()).map(User::getIdUser)
                .orElseThrow(() -> new IllegalArgumentException("postedBy es requerido"));

        projectRepository.findById(idProject).orElseThrow(() -> new ModelNotFoundException("Project no existe"));
        userRepository.findById(idApprovedBy).orElseThrow(() -> new ModelNotFoundException("approvedBy no existe"));
        userRepository.findById(idPostedBy).orElseThrow(() -> new ModelNotFoundException("postedBy no existe"));

        header.setIdMaterialDevengation(null);
        header.setStatus(1);
        header.setDevengationState(header.getDevengationState() != null ? header.getDevengationState()
                : MaterialDevengationState.PENDIENTE);

        MaterialDevengation saved = repository.save(header);

        // Validar y aplicar egresos (mueve stock + kardex)
        for (MaterialDevengationItem it : (items == null ? List.<MaterialDevengationItem>of() : items)) {
            UUID idMat = Optional.ofNullable(it.getMaterial()).map(Material::getIdMaterial)
                    .orElseThrow(() -> new IllegalArgumentException("material es requerido"));
            materialRepository.findById(idMat).orElseThrow(() -> new ModelNotFoundException("Material no existe"));

            if (it.getQuantityConsumed() == null || it.getQuantityConsumed().signum() <= 0)
                throw new IllegalArgumentException("quantityConsumed debe ser > 0");

            ProjectMaterialStock stock = getOrCreateStock(idProject, idMat);
            // Verificar disponibilidad
            if (stock.getQuantityOnHand().compareTo(it.getQuantityConsumed()) < 0) {
                throw new IllegalStateException("Stock insuficiente para material " + idMat);
            }
            // Egreso
            stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(it.getQuantityConsumed()));
            stockRepository.save(stock);

            // Kardex (egreso negativo)
            addMovement(idProject, idMat, it.getQuantityConsumed().negate(), "Devengación", saved.getIdMaterialDevengation());

            // Guardar item
            it.setIdMaterialDevengationItem(null);
            it.setMaterialDevengation(saved);
            it.setStatus(1);
            itemRepository.save(it);
        }

        // Publicado al momento de registrar (si prefieres, puedes dejar PENDIENTE y tener un /publish)
        saved.setDevengationState(MaterialDevengationState.PUBLICADO);
        return repository.save(saved);
    }

    @Override
    @Transactional
    public MaterialDevengation updateWithItems(UUID idDevengation, MaterialDevengation header, List<MaterialDevengationItem> items) throws Exception {
        MaterialDevengation current = repository.findById(idDevengation)
                .orElseThrow(() -> new ModelNotFoundException("MaterialDevengation no existe"));

        UUID idProject = current.getProject().getIdProject();

        // Cargar existentes
        List<MaterialDevengationItem> existing = itemRepository
                .findByMaterialDevengation_IdMaterialDevengationAndStatusNot(idDevengation, 0);
        Map<UUID, MaterialDevengationItem> byMaterial = new HashMap<>();
        existing.forEach(e -> byMaterial.put(e.getMaterial().getIdMaterial(), e));

        Set<UUID> seen = new HashSet<>();

        // Aplicar deltas
        for (MaterialDevengationItem in : (items == null ? List.<MaterialDevengationItem>of() : items)) {
            UUID idMat = Optional.ofNullable(in.getMaterial()).map(Material::getIdMaterial)
                    .orElseThrow(() -> new IllegalArgumentException("material es requerido"));

            BigDecimal newQty = in.getQuantityConsumed();
            if (newQty == null || newQty.signum() < 0) throw new IllegalArgumentException("quantityConsumed inválido");

            MaterialDevengationItem target = byMaterial.get(idMat);
            BigDecimal oldQty = target == null ? BigDecimal.ZERO : target.getQuantityConsumed();
            BigDecimal delta = newQty.subtract(oldQty); // si delta>0 egresa más; si delta<0 regresa stock

            if (delta.signum() != 0) {
                ProjectMaterialStock stock = getOrCreateStock(idProject, idMat);

                if (delta.signum() > 0) {
                    // egreso adicional
                    if (stock.getQuantityOnHand().compareTo(delta) < 0)
                        throw new IllegalStateException("Stock insuficiente para material " + idMat);
                    stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(delta));
                } else {
                    // devolución al stock (reversa de egreso)
                    stock.setQuantityOnHand(stock.getQuantityOnHand().add(delta.negate()));
                }
                stockRepository.save(stock);

                addMovement(idProject, idMat, delta.negate(), "Ajuste devengación", idDevengation);
            }

            if (target == null) {
                target = new MaterialDevengationItem();
                target.setIdMaterialDevengationItem(null);
                target.setMaterialDevengation(current);
                target.setMaterial(in.getMaterial());
                target.setStatus(1);
            }
            target.setQuantityConsumed(newQty);
            target.setObservation(in.getObservation());
            itemRepository.save(target);
            seen.add(idMat);
        }

        // ítems removidos → revertir completamente
        for (MaterialDevengationItem e : existing) {
            UUID idMat = e.getMaterial().getIdMaterial();
            if (!seen.contains(idMat)) {
                // devolver al stock lo previamente egresado
                ProjectMaterialStock stock = getOrCreateStock(idProject, idMat);
                stock.setQuantityOnHand(stock.getQuantityOnHand().add(e.getQuantityConsumed()));
                stockRepository.save(stock);
                addMovement(idProject, idMat, e.getQuantityConsumed(), "Reversa por eliminación de ítem", idDevengation);
                e.setStatus(0);
                itemRepository.save(e);
            }
        }

        // Campos editables de cabecera
        current.setObservation(header.getObservation());
        if (header.getDevengationState() != null) current.setDevengationState(header.getDevengationState());
        if (header.getPostedBy() != null) current.setPostedBy(header.getPostedBy());
        if (header.getApprovedBy() != null) current.setApprovedBy(header.getApprovedBy());

        return repository.save(current);
    }

    @Override
    @Transactional
    public boolean delete(UUID id) throws Exception {
        MaterialDevengation current = repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("MaterialDevengation no existe"));

        UUID idProject = current.getProject().getIdProject();

        // Revertir ítems (stock + kardex)
        List<MaterialDevengationItem> items = itemRepository
                .findByMaterialDevengation_IdMaterialDevengationAndStatusNot(id, 0);

        for (MaterialDevengationItem e : items) {
            UUID idMat = e.getMaterial().getIdMaterial();

            ProjectMaterialStock stock = getOrCreateStock(idProject, idMat);
            stock.setQuantityOnHand(stock.getQuantityOnHand().add(e.getQuantityConsumed()));
            stockRepository.save(stock);

            // kardex con signo POSITIVO (reversa del egreso)
            addMovement(idProject, idMat, e.getQuantityConsumed(), "Reversa por eliminación de devengación", id);

            e.setStatus(0);
            itemRepository.save(e);
        }

        // Marcar cabecera como eliminada
        current.setStatus(0);
        repository.save(current);

        return true;
    }
}
