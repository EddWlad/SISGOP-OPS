package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.ContractorConsumptionApprovalDTO;
import com.tidsec.sisgop_backend.dto.ContractorConsumptionDTO;
import com.tidsec.sisgop_backend.dto.ContractorConsumptionItemDTO;
import com.tidsec.sisgop_backend.dto.enums.ContractorConsumptionState;
import com.tidsec.sisgop_backend.entity.ContractorConsumption;
import com.tidsec.sisgop_backend.entity.ContractorConsumptionItem;
import com.tidsec.sisgop_backend.service.IContractorConsumptionService;
import com.tidsec.sisgop_backend.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contractor-consumptions")
public class ContractorConsumptionController {
    private final IContractorConsumptionService service;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<ContractorConsumptionDTO>> findAll() throws Exception {
        List<ContractorConsumption> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, ContractorConsumptionDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<ContractorConsumptionDTO> findById(@PathVariable UUID id) throws Exception {
        ContractorConsumption obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, ContractorConsumptionDTO.class));
    }

    // Filtros
    @PreAuthorize("@authorizeLogic.hasAccess('findByProject')")
    @GetMapping("/by-project/{idProject}")
    public ResponseEntity<List<ContractorConsumptionDTO>> byProject(@PathVariable UUID idProject) throws Exception {
        List<ContractorConsumption> list = service.findByProject(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, ContractorConsumptionDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findByState')")
    @GetMapping("/by-state/{state}")
    public ResponseEntity<List<ContractorConsumptionDTO>> byState(@PathVariable ContractorConsumptionState state) throws Exception {
        List<ContractorConsumption> list = service.findByState(state);
        return ResponseEntity.ok(mapperUtil.mapList(list, ContractorConsumptionDTO.class));
    }

    // Items por cabecera
    @PreAuthorize("@authorizeLogic.hasAccess('findItems')")
    @GetMapping("/{id}/items")
    public ResponseEntity<List<ContractorConsumptionItemDTO>> items(@PathVariable UUID id) throws Exception {
        List<ContractorConsumptionItem> items = service.findItems(id);
        return ResponseEntity.ok(mapperUtil.mapList(items, ContractorConsumptionItemDTO.class));
    }

    // Crear con items
    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<ContractorConsumptionDTO> save(@Valid @RequestBody ContractorConsumptionDTO dto) throws Exception {
        ContractorConsumption header = mapperUtil.map(dto, ContractorConsumption.class);
        List<ContractorConsumptionItem> items = dto.getItems() == null
                ? Collections.emptyList()
                : mapperUtil.mapList(dto.getItems(), ContractorConsumptionItem.class);

        ContractorConsumption saved = service.saveWithItems(header, items);
        return ResponseEntity.ok(mapperUtil.map(saved, ContractorConsumptionDTO.class));
    }

    // Update con items
    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<ContractorConsumptionDTO> update(@PathVariable UUID id,
                                                           @Valid @RequestBody ContractorConsumptionDTO dto) throws Exception {
        dto.setIdContractorConsumption(id);
        ContractorConsumption header = mapperUtil.map(dto, ContractorConsumption.class);
        List<ContractorConsumptionItem> items = dto.getItems() == null
                ? Collections.emptyList()
                : mapperUtil.mapList(dto.getItems(), ContractorConsumptionItem.class);

        ContractorConsumption saved = service.updateWithItems(id, header, items);
        return ResponseEntity.ok(mapperUtil.map(saved, ContractorConsumptionDTO.class));
    }

    // Acciones de flujo: send / approve / reject
    @PreAuthorize("@authorizeLogic.hasAccess('send')")
    @PostMapping("/{id}/send")
    public ResponseEntity<ContractorConsumptionDTO> send(@PathVariable UUID id) throws Exception {
        ContractorConsumption saved = service.send(id);
        return ResponseEntity.ok(mapperUtil.map(saved, ContractorConsumptionDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('approve')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ContractorConsumptionDTO> approve(
            @PathVariable UUID id,
            @Valid @RequestBody List<ContractorConsumptionApprovalDTO> approvalsDTO) throws Exception {

        // Map directo solo con lo necesario
        List<ContractorConsumptionItem> approvals = approvalsDTO.stream().map(d -> {
            ContractorConsumptionItem it = new ContractorConsumptionItem();
            it.setMaterial(d.getMaterial());
            it.setQuantityApproved(d.getQuantityApproved());
            return it;
        }).toList();

        ContractorConsumption saved = service.approve(id, approvals);
        return ResponseEntity.ok(mapperUtil.map(saved, ContractorConsumptionDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('reject')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ContractorConsumptionDTO> reject(@PathVariable UUID id,
                                                           @RequestBody(required = false) String reason) throws Exception {
        ContractorConsumption saved = service.reject(id, reason);
        return ResponseEntity.ok(mapperUtil.map(saved, ContractorConsumptionDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
