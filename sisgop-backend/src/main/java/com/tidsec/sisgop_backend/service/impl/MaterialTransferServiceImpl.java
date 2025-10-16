package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.dto.enums.TransferReceiptState;
import com.tidsec.sisgop_backend.dto.enums.TransferState;
import com.tidsec.sisgop_backend.entity.Material;
import com.tidsec.sisgop_backend.entity.MaterialTransfer;
import com.tidsec.sisgop_backend.entity.MaterialTransferItem;
import com.tidsec.sisgop_backend.dto.enums.MovementStockType;
import com.tidsec.sisgop_backend.entity.Project;
import com.tidsec.sisgop_backend.entity.ProjectMaterialStock;
import com.tidsec.sisgop_backend.entity.StockMovement;
import com.tidsec.sisgop_backend.entity.User;
import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IMaterialRepository;
import com.tidsec.sisgop_backend.repository.IMaterialTransferItemRepository;
import com.tidsec.sisgop_backend.repository.IMaterialTransferRepository;
import com.tidsec.sisgop_backend.repository.IProjectMaterialStockRepository;
import com.tidsec.sisgop_backend.repository.IProjectRepository;
import com.tidsec.sisgop_backend.repository.IStockMovementRepository;
import com.tidsec.sisgop_backend.repository.IUserRepository;
import com.tidsec.sisgop_backend.service.IMaterialTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialTransferServiceImpl extends GenericServiceImpl<MaterialTransfer, UUID> implements IMaterialTransferService {

    private final IMaterialTransferRepository transferRepository;
    private final IMaterialTransferItemRepository itemRepository;

    private final IProjectMaterialStockRepository projectMaterialStockRepository;
    private final IStockMovementRepository stockMovementRepository;

    private final IMaterialRepository materialRepository;
    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;

    @Override
    protected IGenericRepository<MaterialTransfer, UUID> getRepo() {
        return transferRepository;
    }

    // ===================== CONSULTAS =====================

    @Override
    public List<MaterialTransfer> findBySource(UUID idSourceProject) throws Exception {
        return transferRepository.findBySourceProject_IdProjectAndStatusNot(idSourceProject, 0);
    }

    @Override
    public List<MaterialTransfer> findByTarget(UUID idTargetProject) throws Exception {
        return transferRepository.findByTargetProject_IdProjectAndStatusNot(idTargetProject, 0);
    }

    @Override
    public List<MaterialTransferItem> findItems(UUID idMaterialTransfer) throws Exception {
        return itemRepository.findByMaterialTransfer_IdMaterialTransferAndStatusNot(idMaterialTransfer, 0);
    }

    @Override
    public Page<MaterialTransfer> listPage(Pageable pageable) throws Exception {
        return transferRepository.findAll(pageable);
    }

    // ===================== CREATE / UPDATE (BORRADOR) =====================

    @Override
    @Transactional
    public MaterialTransfer createWithItems(MaterialTransfer header, List<MaterialTransferItem> items) throws Exception {
        if (header == null) throw new IllegalArgumentException("header es requerido");
        if (header.getSourceProject() == null || header.getTargetProject() == null)
            throw new IllegalArgumentException("sourceProject y targetProject son requeridos");

        UUID idSrc = header.getSourceProject().getIdProject();
        UUID idDst = header.getTargetProject().getIdProject();
        if (Objects.equals(idSrc, idDst))
            throw new IllegalArgumentException("El proyecto ORIGEN y DESTINO no pueden ser el mismo");

        UUID idReq = Optional.ofNullable(header.getRequestedBy()).map(User::getIdUser)
                .orElseThrow(() -> new IllegalArgumentException("requestedBy es requerido"));

        Project src = projectRepository.findById(idSrc)
                .orElseThrow(() -> new ModelNotFoundException("Proyecto ORIGEN no existe: " + idSrc));
        Project dst = projectRepository.findById(idDst)
                .orElseThrow(() -> new ModelNotFoundException("Proyecto DESTINO no existe: " + idDst));
        User requestedBy = userRepository.findById(idReq)
                .orElseThrow(() -> new ModelNotFoundException("Usuario requestedBy no existe: " + idReq));

        header.setSourceProject(src);
        header.setTargetProject(dst);
        header.setRequestedBy(requestedBy);
        header.setTransferState(TransferState.BORRADOR);
        header.setDestinationReceiptState(TransferReceiptState.PENDIENTE);

        if (header.getTransferCode() == null || header.getTransferCode().isBlank()) {
            header.setTransferCode(generateTransferCode());
        }

        MaterialTransfer saved = transferRepository.save(header);

        if (items != null && !items.isEmpty()) {
            for (MaterialTransferItem it : items) {
                UUID idMat = Optional.ofNullable(it.getMaterial()).map(Material::getIdMaterial)
                        .orElseThrow(() -> new IllegalArgumentException("material es requerido en items"));
                Material m = materialRepository.findById(idMat)
                        .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMat));

                if (it.getQuantityRequested() == null || it.getQuantityRequested().signum() <= 0)
                    throw new IllegalArgumentException("quantityRequested debe ser > 0");

                if (it.getQuantityTransferred() == null) it.setQuantityTransferred(BigDecimal.ZERO);

                it.setMaterialTransfer(saved);
                it.setMaterial(m);
                it.setStatus(1);
                itemRepository.save(it);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public MaterialTransfer updateWithItems(MaterialTransfer header, List<MaterialTransferItem> newItems) throws Exception {
        if (header == null || header.getIdMaterialTransfer() == null)
            throw new IllegalArgumentException("idMaterialTransfer es requerido");

        MaterialTransfer current = transferRepository.findById(header.getIdMaterialTransfer())
                .orElseThrow(() -> new ModelNotFoundException("MaterialTransfer no existe: " + header.getIdMaterialTransfer()));

        if (current.getTransferState() != TransferState.BORRADOR) {
            throw new IllegalStateException("Solo se puede editar en estado BORRADOR");
        }

        if (header.getObservation() != null) current.setObservation(header.getObservation());

        if (header.getSourceProject() != null && header.getTargetProject() != null) {
            UUID idSrc = header.getSourceProject().getIdProject();
            UUID idDst = header.getTargetProject().getIdProject();
            if (Objects.equals(idSrc, idDst)) throw new IllegalArgumentException("ORIGEN y DESTINO no pueden ser iguales");

            Project src = projectRepository.findById(idSrc)
                    .orElseThrow(() -> new ModelNotFoundException("Proyecto ORIGEN no existe: " + idSrc));
            Project dst = projectRepository.findById(idDst)
                    .orElseThrow(() -> new ModelNotFoundException("Proyecto DESTINO no existe: " + idDst));

            current.setSourceProject(src);
            current.setTargetProject(dst);
        }

        if (header.getRequestedBy() != null) {
            UUID idReq = header.getRequestedBy().getIdUser();
            User req = userRepository.findById(idReq)
                    .orElseThrow(() -> new ModelNotFoundException("requestedBy no existe: " + idReq));
            current.setRequestedBy(req);
        }

        // merge/reemplazo de ítems
        List<MaterialTransferItem> existing = itemRepository
                .findByMaterialTransfer_IdMaterialTransferAndStatusNot(current.getIdMaterialTransfer(), 0);

        Map<UUID, MaterialTransferItem> byMaterial = existing.stream()
                .collect(Collectors.toMap(it -> it.getMaterial().getIdMaterial(), it -> it));

        Set<UUID> touched = new HashSet<>();
        if (newItems != null) {
            for (MaterialTransferItem in : newItems) {
                UUID idMat = Optional.ofNullable(in.getMaterial()).map(Material::getIdMaterial)
                        .orElseThrow(() -> new IllegalArgumentException("material es requerido en items"));

                Material material = materialRepository.findById(idMat)
                        .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMat));

                BigDecimal qReq = Optional.ofNullable(in.getQuantityRequested())
                        .orElseThrow(() -> new IllegalArgumentException("quantityRequested requerido"));
                if (qReq.signum() <= 0) throw new IllegalArgumentException("quantityRequested debe ser > 0");

                MaterialTransferItem target = byMaterial.get(idMat);
                if (target == null) {
                    target = MaterialTransferItem.builder()
                            .materialTransfer(current)
                            .material(material)
                            .quantityRequested(qReq)
                            .quantityTransferred(BigDecimal.ZERO)
                            .status(1)
                            .observation(in.getObservation())
                            .build();
                } else {
                    target.setQuantityRequested(qReq);
                    target.setObservation(in.getObservation());
                    if (target.getQuantityTransferred() == null) target.setQuantityTransferred(BigDecimal.ZERO);
                }
                itemRepository.save(target);
                touched.add(idMat);
            }
        }

        // Soft delete de ítems que ya no vienen
        for (MaterialTransferItem it : existing) {
            UUID idMat = it.getMaterial().getIdMaterial();
            if (!touched.contains(idMat)) {
                it.setStatus(0);
                itemRepository.save(it);
            }
        }

        return transferRepository.save(current);
    }

    // ===================== AUTORIZAR =====================

    @Override
    @Transactional
    public MaterialTransfer authorize(UUID idMaterialTransfer, UUID idUserWhoAuthorizes) throws Exception {
        MaterialTransfer trf = transferRepository.findById(idMaterialTransfer)
                .orElseThrow(() -> new ModelNotFoundException("MaterialTransfer no existe: " + idMaterialTransfer));

        if (trf.getTransferState() != TransferState.BORRADOR) {
            throw new IllegalStateException("Solo se puede autorizar desde BORRADOR");
        }

        List<MaterialTransferItem> items = itemRepository
                .findByMaterialTransfer_IdMaterialTransferAndStatusNot(idMaterialTransfer, 0);
        if (items.isEmpty()) throw new IllegalStateException("No se puede autorizar sin ítems");

        boolean anyInvalid = items.stream().anyMatch(it -> it.getQuantityRequested() == null || it.getQuantityRequested().signum() <= 0);
        if (anyInvalid) throw new IllegalStateException("Todos los ítems deben tener quantityRequested > 0");

        if (idUserWhoAuthorizes != null) {
            User u = userRepository.findById(idUserWhoAuthorizes)
                    .orElseThrow(() -> new ModelNotFoundException("Usuario no existe: " + idUserWhoAuthorizes));
            trf.setRequestedBy(u); // residente que autoriza
        }

        trf.setTransferState(TransferState.AUTORIZADA);
        return transferRepository.save(trf);
    }

    // ===================== EJECUTAR (mueve stock + kardex) =====================

    @Override
    @Transactional
    public MaterialTransfer execute(UUID idMaterialTransfer, List<MaterialTransferItem> execItems, UUID idUserWhoExecutes) throws Exception {
        MaterialTransfer trf = transferRepository.findById(idMaterialTransfer)
                .orElseThrow(() -> new ModelNotFoundException("MaterialTransfer no existe: " + idMaterialTransfer));

        if (trf.getTransferState() != TransferState.AUTORIZADA) {
            throw new IllegalStateException("Solo se puede ejecutar desde AUTORIZADA");
        }

        List<MaterialTransferItem> items = itemRepository
                .findByMaterialTransfer_IdMaterialTransferAndStatusNot(idMaterialTransfer, 0);
        if (items.isEmpty()) throw new IllegalStateException("No hay ítems para ejecutar");

        Map<UUID, MaterialTransferItem> byMaterial = items.stream()
                .collect(Collectors.toMap(it -> it.getMaterial().getIdMaterial(), it -> it));

        for (MaterialTransferItem in : execItems) {
            UUID idMat = Optional.ofNullable(in.getMaterial()).map(Material::getIdMaterial)
                    .orElseThrow(() -> new IllegalArgumentException("material es requerido (execute)"));

            // Permitimos mapear quantityToTransfer -> quantityTransferred
            BigDecimal qty = Optional.ofNullable(in.getQuantityTransferred())
                    .orElseGet(in::getQuantityRequested);
            if (qty == null) throw new IllegalArgumentException("quantityToTransfer es requerido");
            if (qty.signum() <= 0) continue;

            MaterialTransferItem target = Optional.ofNullable(byMaterial.get(idMat))
                    .orElseThrow(() -> new IllegalArgumentException("Material no pertenece a la transferencia"));

            BigDecimal already = Optional.ofNullable(target.getQuantityTransferred()).orElse(BigDecimal.ZERO);
            BigDecimal remaining = target.getQuantityRequested().subtract(already);
            if (qty.compareTo(remaining) > 0) {
                throw new IllegalStateException("quantityToTransfer excede lo solicitado pendiente");
            }

            // ORIGEN: resta
            Project src = trf.getSourceProject();
            ProjectMaterialStock stockSrc = ensureStockRow(src.getIdProject(), idMat);
            if (stockSrc.getQuantityOnHand() == null) stockSrc.setQuantityOnHand(BigDecimal.ZERO);
            if (stockSrc.getQuantityOnHand().compareTo(qty) < 0) {
                throw new IllegalStateException("Stock insuficiente en proyecto ORIGEN para material " + idMat);
            }
            stockSrc.setQuantityOnHand(stockSrc.getQuantityOnHand().subtract(qty));
            projectMaterialStockRepository.save(stockSrc);

            appendMovement(src, idMat, qty.negate(), MovementStockType.EGRESO_TRANSFERENCIA,
                    "Transferencia " + trf.getTransferCode(), trf.getIdMaterialTransfer());

            // DESTINO: suma
            Project dst = trf.getTargetProject();
            ProjectMaterialStock stockDst = ensureStockRow(dst.getIdProject(), idMat);
            if (stockDst.getQuantityOnHand() == null) stockDst.setQuantityOnHand(BigDecimal.ZERO);
            stockDst.setQuantityOnHand(stockDst.getQuantityOnHand().add(qty));
            projectMaterialStockRepository.save(stockDst);

            appendMovement(dst, idMat, qty, MovementStockType.INGRESO_TRANSFERENCIA,
                    "Transferencia " + trf.getTransferCode(), trf.getIdMaterialTransfer());

            // actualizar ítem
            target.setQuantityTransferred(already.add(qty));
            itemRepository.save(target);
        }

        // evaluar estado final
        List<MaterialTransferItem> after = itemRepository
                .findByMaterialTransfer_IdMaterialTransferAndStatusNot(idMaterialTransfer, 0);

        boolean allDone = after.stream().allMatch(it ->
                Optional.ofNullable(it.getQuantityTransferred()).orElse(BigDecimal.ZERO)
                        .compareTo(it.getQuantityRequested()) >= 0);

        boolean anyTransferred = after.stream().anyMatch(it ->
                Optional.ofNullable(it.getQuantityTransferred()).orElse(BigDecimal.ZERO).signum() > 0);

        if (idUserWhoExecutes != null) {
            User u = userRepository.findById(idUserWhoExecutes)
                    .orElseThrow(() -> new ModelNotFoundException("Usuario no existe: " + idUserWhoExecutes));
            trf.setExecutedBy(u);
        }
        trf.setExecutionDate(LocalDateTime.now());

        if (allDone) trf.setTransferState(TransferState.COMPLETADA);
        else if (anyTransferred) trf.setTransferState(TransferState.PARCIAL);

        return transferRepository.save(trf);
    }

    // ===================== ACUSE EN DESTINO (opcional) =====================

    @Override
    @Transactional
    public MaterialTransfer confirmReceipt(UUID idMaterialTransfer,
                                           TransferReceiptState receiptState,
                                           String headerObservation,
                                           List<MaterialTransferItem> destAckItems,
                                           UUID idUserWhoReceives) throws Exception {
        MaterialTransfer trf = transferRepository.findById(idMaterialTransfer)
                .orElseThrow(() -> new ModelNotFoundException("MaterialTransfer no existe: " + idMaterialTransfer));

        if (trf.getTransferState() != TransferState.PARCIAL
                && trf.getTransferState() != TransferState.COMPLETADA) {
            throw new IllegalStateException("Solo se puede confirmar recepción si la transferencia fue ejecutada");
        }

        if (receiptState == null || (receiptState != TransferReceiptState.RECIBIDO
                && receiptState != TransferReceiptState.RECIBIDO_NOVEDADES)) {
            throw new IllegalArgumentException("receiptState inválido");
        }

        List<MaterialTransferItem> items = itemRepository
                .findByMaterialTransfer_IdMaterialTransferAndStatusNot(idMaterialTransfer, 0);
        Map<UUID, MaterialTransferItem> byMaterial = items.stream()
                .collect(Collectors.toMap(it -> it.getMaterial().getIdMaterial(), it -> it));

        boolean anyNovelty = false;
        boolean allChecked = true;

        if (destAckItems != null) {
            for (MaterialTransferItem in : destAckItems) {
                UUID idMat = Optional.ofNullable(in.getMaterial()).map(Material::getIdMaterial)
                        .orElseThrow(() -> new IllegalArgumentException("material es requerido (acuse)"));

                MaterialTransferItem target = Optional.ofNullable(byMaterial.get(idMat))
                        .orElseThrow(() -> new IllegalArgumentException("Material no pertenece a la transferencia"));

                target.setDestinationChecked(in.getDestinationChecked());
                target.setDestinationObservation(in.getDestinationObservation());
                target.setQuantityReceivedDestination(in.getQuantityReceivedDestination());
                itemRepository.save(target);

                boolean checked = Boolean.TRUE.equals(in.getDestinationChecked());
                String obs = in.getDestinationObservation();

                if (!checked) allChecked = false;
                if ((obs != null && !obs.isBlank()) || in.getQuantityReceivedDestination() != null)
                    anyNovelty = true;
            }
        }

        if (receiptState == TransferReceiptState.RECIBIDO) {
            if (!allChecked || anyNovelty) {
                throw new IllegalStateException("Para RECIBIDO, todos los ítems deben estar verificados y sin novedades.");
            }
        } else { // RECIBIDO_NOVEDADES
            if (!anyNovelty && allChecked) {
                throw new IllegalStateException("Para RECIBIDO_NOVEDADES, registra al menos una novedad en los ítems.");
            }
        }

        trf.setDestinationReceiptState(receiptState);
        trf.setDestinationObservation(headerObservation);
        trf.setReceptionDate(LocalDateTime.now());

        if (idUserWhoReceives != null) {
            User u = userRepository.findById(idUserWhoReceives)
                    .orElseThrow(() -> new ModelNotFoundException("Usuario no existe: " + idUserWhoReceives));
            trf.setReceivedBy(u);
        }

        return transferRepository.save(trf);
    }

    // ===================== HELPERS =====================

    private String generateTransferCode() {
        // Integra tu servicio de códigos aquí (p.ej., codeService.next("TRF")).
        LocalDateTime now = LocalDateTime.now();
        return String.format("TRF-%04d%02d%02d-%02d%02d%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond());
    }

    private ProjectMaterialStock ensureStockRow(UUID idProject, UUID idMaterial) {
        return projectMaterialStockRepository
                .findByProject_IdProjectAndMaterial_IdMaterial(idProject, idMaterial)
                .orElseGet(() -> {
                    Project project = projectRepository.findById(idProject)
                            .orElseThrow(() -> new ModelNotFoundException("Proyecto no existe: " + idProject));
                    Material material = materialRepository.findById(idMaterial)
                            .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMaterial));
                    ProjectMaterialStock s = new ProjectMaterialStock();
                    s.setProject(project);
                    s.setMaterial(material);
                    s.setQuantityOnHand(BigDecimal.ZERO); // campo correcto en tu entidad
                    s.setStatus(1);
                    return projectMaterialStockRepository.save(s);
                });
    }

    private void appendMovement(Project project,
                                UUID idMaterial,
                                BigDecimal qtySigned,
                                MovementStockType type,
                                String observation,
                                UUID referenceId) {
        StockMovement mv = new StockMovement();
        mv.setMaterial(materialRepository.findById(idMaterial)
                .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMaterial)));
        mv.setProject(project);
        mv.setMovementDate(LocalDateTime.now());
        mv.setMovementType(type);
        mv.setQuantity(qtySigned); // NEGATIVO para EGRESO, POSITIVO para INGRESO
        mv.setObservation(observation);
        mv.setReferenceType("TRANSFERENCIA");
        mv.setReferenceId(referenceId);
        mv.setStatus(1);
        stockMovementRepository.save(mv);
    }
}