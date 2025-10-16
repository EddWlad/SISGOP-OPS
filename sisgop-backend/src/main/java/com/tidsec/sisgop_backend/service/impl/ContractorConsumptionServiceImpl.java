package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.dto.enums.ContractorConsumptionState;
import com.tidsec.sisgop_backend.entity.*;
import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.*;
import com.tidsec.sisgop_backend.service.IContractorConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContractorConsumptionServiceImpl extends GenericServiceImpl<ContractorConsumption, UUID>
        implements IContractorConsumptionService{

    private final IContractorConsumptionRepository repository;
    private final IContractorConsumptionItemRepository itemRepository;
    private final IProjectRepository projectRepository;
    private final IUserRepository userRepository;

    @Override
    protected IGenericRepository<ContractorConsumption, UUID> getRepo() {
        return repository;
    }

    @Override
    public List<ContractorConsumption> findByProject(UUID idProject) {
        return repository.findByProject_IdProjectAndStatusNot(idProject, 0);
    }

    @Override
    public List<ContractorConsumption> findByState(ContractorConsumptionState state) {
        return repository.findByConsumptionStateAndStatusNot(state, 0);
    }

    @Override
    public List<ContractorConsumption> findByProjectAndStates(UUID idProject, List<ContractorConsumptionState> states) {
        return repository.findByProject_IdProjectAndConsumptionStateInAndStatusNot(idProject, states, 0);
    }

    @Override
    public List<ContractorConsumption> findByReportedBy(UUID idUser) {
        return repository.findByReportedBy_IdUserAndStatusNot(idUser, 0);
    }

    @Override
    public List<ContractorConsumptionItem> findItems(UUID idConsumption) {
        return itemRepository.findByContractorConsumption_IdContractorConsumptionAndStatusNot(idConsumption, 0);
    }

    @Override
    @Transactional
    public ContractorConsumption saveWithItems(ContractorConsumption header, List<ContractorConsumptionItem> items) throws Exception {
        UUID idProject = Optional.ofNullable(header.getProject()).map(Project::getIdProject)
                .orElseThrow(() -> new IllegalArgumentException("project es requerido"));
        UUID idUser = Optional.ofNullable(header.getReportedBy()).map(User::getIdUser)
                .orElseThrow(() -> new IllegalArgumentException("reportedBy es requerido"));
        projectRepository.findById(idProject).orElseThrow(() -> new ModelNotFoundException("Project no existe"));
        userRepository.findById(idUser).orElseThrow(() -> new ModelNotFoundException("User no existe"));

        header.setIdContractorConsumption(null);
        header.setStatus(1);
        header.setConsumptionState(header.getConsumptionState() != null ? header.getConsumptionState()
                : ContractorConsumptionState.BORRADOR);

        ContractorConsumption saved = repository.save(header);

        // Upsert por material (único por cabecera+material)
        Map<UUID, ContractorConsumptionItem> toSave = new LinkedHashMap<>();
        for (ContractorConsumptionItem it : (items == null ? List.<ContractorConsumptionItem>of() : items)) {
            UUID idMat = Optional.ofNullable(it.getMaterial()).map(Material::getIdMaterial)
                    .orElseThrow(() -> new IllegalArgumentException("material es requerido"));
            it.setIdContractorConsumptionItem(null);
            it.setContractorConsumption(saved);
            it.setStatus(1);
            if (it.getQuantityApproved() == null) it.setQuantityApproved(BigDecimal.ZERO);
            toSave.put(idMat, it); // merge por material
        }
        if (!toSave.isEmpty()) itemRepository.saveAll(toSave.values());
        return saved;
    }

    @Override
    @Transactional
    public ContractorConsumption updateWithItems(UUID idConsumption, ContractorConsumption header, List<ContractorConsumptionItem> items) throws Exception {
        ContractorConsumption current = repository.findById(idConsumption)
                .orElseThrow(() -> new ModelNotFoundException("ContractorConsumption no existe"));

        // Permitir editar solo si no está RECHAZADO
        if (current.getConsumptionState() == ContractorConsumptionState.RECHAZADO) {
            throw new IllegalStateException("No se puede editar en estado RECHAZADO");
        }

        // Actualizar cabecera (solo campos editables)
        current.setObservation(header.getObservation());
        if (header.getConsumptionState() != null) current.setConsumptionState(header.getConsumptionState());

        // Sincronizar items (upsert y soft-delete de faltantes)
        List<ContractorConsumptionItem> existing = itemRepository
                .findByContractorConsumption_IdContractorConsumptionAndStatusNot(idConsumption, 0);
        Map<UUID, ContractorConsumptionItem> byMaterial = new HashMap<>();
        for (ContractorConsumptionItem e : existing) byMaterial.put(e.getMaterial().getIdMaterial(), e);

        Set<UUID> seen = new HashSet<>();
        for (ContractorConsumptionItem in : (items == null ? List.<ContractorConsumptionItem>of() : items)) {
            UUID idMat = Optional.ofNullable(in.getMaterial()).map(Material::getIdMaterial)
                    .orElseThrow(() -> new IllegalArgumentException("material es requerido"));
            seen.add(idMat);
            ContractorConsumptionItem target = byMaterial.getOrDefault(idMat, new ContractorConsumptionItem());
            target.setContractorConsumption(current);
            target.setMaterial(in.getMaterial());
            target.setQuantityUsed(in.getQuantityUsed());
            target.setQuantityApproved(in.getQuantityApproved() == null ? BigDecimal.ZERO : in.getQuantityApproved());
            target.setObservation(in.getObservation());
            target.setStatus(1);
            itemRepository.save(target);
        }
        for (ContractorConsumptionItem e : existing) {
            if (!seen.contains(e.getMaterial().getIdMaterial())) {
                e.setStatus(0);
                itemRepository.save(e);
            }
        }
        return repository.save(current);
    }

    @Override
    @Transactional
    public ContractorConsumption send(UUID idConsumption) throws Exception {
        ContractorConsumption c = repository.findById(idConsumption)
                .orElseThrow(() -> new ModelNotFoundException("ContractorConsumption no existe"));

        if (c.getConsumptionState() == ContractorConsumptionState.RECHAZADO) {
            throw new IllegalStateException("No se puede enviar en estado RECHAZADO");
        }
        // Debe tener items
        List<ContractorConsumptionItem> items = itemRepository
                .findByContractorConsumption_IdContractorConsumptionAndStatusNot(idConsumption, 0);
        if (items.isEmpty()) throw new IllegalStateException("No se puede enviar sin items");

        c.setConsumptionState(ContractorConsumptionState.ENVIADA);
        return repository.save(c);
    }

    @Override
    @Transactional
    public ContractorConsumption approve(UUID idConsumption, List<ContractorConsumptionItem> approvals) throws Exception {
        ContractorConsumption c = repository.findById(idConsumption)
                .orElseThrow(() -> new ModelNotFoundException("ContractorConsumption no existe"));

        List<ContractorConsumptionItem> items = itemRepository
                .findByContractorConsumption_IdContractorConsumptionAndStatusNot(idConsumption, 0);
        Map<UUID, ContractorConsumptionItem> byMaterial = new HashMap<>();
        for (ContractorConsumptionItem e : items) byMaterial.put(e.getMaterial().getIdMaterial(), e);

        for (ContractorConsumptionItem in : (approvals == null ? List.<ContractorConsumptionItem>of() : approvals)) {
            UUID idMat = Optional.ofNullable(in.getMaterial()).map(Material::getIdMaterial)
                    .orElseThrow(() -> new IllegalArgumentException("material es requerido"));
            ContractorConsumptionItem target = Optional.ofNullable(byMaterial.get(idMat))
                    .orElseThrow(() -> new IllegalArgumentException("Material no pertenece al consumo"));
            // No mover stock aquí. Solo ajustar quantityApproved (no mayor a used)
            if (in.getQuantityApproved() == null) continue;
            if (in.getQuantityApproved().compareTo(target.getQuantityUsed()) > 0) {
                throw new IllegalArgumentException("Aprobado no puede exceder lo usado para el material " + idMat);
            }
            target.setQuantityApproved(in.getQuantityApproved());
            itemRepository.save(target);
        }
        c.setConsumptionState(ContractorConsumptionState.APROBADO);
        return repository.save(c);
    }

    @Override
    @Transactional
    public ContractorConsumption reject(UUID idConsumption, String reason) throws Exception {
        ContractorConsumption c = repository.findById(idConsumption)
                .orElseThrow(() -> new ModelNotFoundException("ContractorConsumption no existe"));
        c.setConsumptionState(ContractorConsumptionState.RECHAZADO);
        if (reason != null && !reason.isBlank()) c.setObservation(reason);
        return repository.save(c);
    }
}
