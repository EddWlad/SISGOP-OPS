package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.MaterialTransferDTO;
import com.tidsec.sisgop_backend.dto.MaterialTransferItemDTO;
import com.tidsec.sisgop_backend.dto.enums.TransferReceiptState;
import com.tidsec.sisgop_backend.entity.MaterialTransfer;
import com.tidsec.sisgop_backend.entity.MaterialTransferItem;
import com.tidsec.sisgop_backend.service.IMaterialTransferService;
import com.tidsec.sisgop_backend.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/material-transfers")
public class MaterialTransferController {
    private final IMaterialTransferService service;
    private final MapperUtil mapperUtil;

    // ================== CRUD básico ==================

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MaterialTransferDTO>> findAll() throws Exception {
        List<MaterialTransfer> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialTransferDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialTransferDTO> findById(@PathVariable("id") UUID id) throws Exception {
        MaterialTransfer obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, MaterialTransferDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<MaterialTransferDTO> save(@Valid @RequestBody MaterialTransferDTO dto) throws Exception {
        // createWithItems (BORRADOR)
        MaterialTransfer entity = mapperUtil.map(dto, MaterialTransfer.class);
        List<MaterialTransferItem> items = mapperUtil.mapList(dto.getItems(), MaterialTransferItem.class);
        MaterialTransfer saved = service.createWithItems(entity, items);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialTransferDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialTransferDTO> update(@PathVariable("id") UUID id,
                                                      @Valid @RequestBody MaterialTransferDTO dto) throws Exception {
        dto.setIdMaterialTransfer(id);
        MaterialTransfer entity = mapperUtil.map(dto, MaterialTransfer.class);
        List<MaterialTransferItem> items = mapperUtil.mapList(dto.getItems(), MaterialTransferItem.class);
        MaterialTransfer saved = service.updateWithItems(entity, items);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialTransferDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) throws Exception {
        service.delete(id); // usa tu soft-delete del genérico
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<MaterialTransferDTO>> listPage(Pageable pageable) throws Exception {
        Page<MaterialTransfer> page = service.listPage(pageable);
        Page<MaterialTransferDTO> pageDTO = page.map(ms -> mapperUtil.map(ms, MaterialTransferDTO.class));
        return ResponseEntity.ok(pageDTO);
    }

    // ================== Consultas específicas ==================

    @PreAuthorize("@authorizeLogic.hasAccess('findByIdSource')")
    @GetMapping("/by-source/{idProject}")
    public ResponseEntity<List<MaterialTransferDTO>> findBySource(@PathVariable("idProject") UUID idProject) throws Exception {
        List<MaterialTransfer> list = service.findBySource(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialTransferDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findByIdTarget')")
    @GetMapping("/by-target/{idProject}")
    public ResponseEntity<List<MaterialTransferDTO>> findByTarget(@PathVariable("idProject") UUID idProject) throws Exception {
        List<MaterialTransfer> list = service.findByTarget(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialTransferDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findItems')")
    @GetMapping("/{id}/items")
    public ResponseEntity<List<MaterialTransferItemDTO>> findItems(@PathVariable("id") UUID id) throws Exception {
        List<MaterialTransferItem> items = service.findItems(id);
        return ResponseEntity.ok(mapperUtil.mapList(items, MaterialTransferItemDTO.class));
    }

    // ================== Flujo de negocio ==================

    // Autorizar (Residente): pasa BORRADOR -> AUTORIZADA
    @PreAuthorize("@authorizeLogic.hasAccess('authorize')")
    @PostMapping("/{id}/authorize")
    public ResponseEntity<MaterialTransferDTO> authorize(@PathVariable("id") UUID id,
                                                         @RequestParam(name = "authorizedBy", required = false) UUID authorizedBy) throws Exception {
        MaterialTransfer out = service.authorize(id, authorizedBy);
        return ResponseEntity.ok(mapperUtil.map(out, MaterialTransferDTO.class));
    }

    // Ejecutar (Contratista Origen): mueve stock y kardex
    @PreAuthorize("@authorizeLogic.hasAccess('execute')")
    @PostMapping("/{id}/execute")
    public ResponseEntity<MaterialTransferDTO> execute(@PathVariable("id") UUID id,
                                                       @Valid @RequestBody List<MaterialTransferItemDTO> body,
                                                       @RequestParam(name = "executedBy", required = false) UUID executedBy) throws Exception {
        List<MaterialTransferItem> execItems = mapperUtil.mapList(body, MaterialTransferItem.class);
        MaterialTransfer out = service.execute(id, execItems, executedBy);
        return ResponseEntity.ok(mapperUtil.map(out, MaterialTransferDTO.class));
    }

    // Confirmación de recepción (Destino): no mueve stock, solo trazabilidad
    @PreAuthorize("@authorizeLogic.hasAccess('confirmReceipt')")
    @PostMapping("/{id}/confirm-receipt")
    public ResponseEntity<MaterialTransferDTO> confirmReceipt(@PathVariable("id") UUID id,
                                                              @RequestParam("state") TransferReceiptState state,
                                                              @RequestParam(name = "receivedBy", required = false) UUID receivedBy,
                                                              @RequestParam(name = "observation", required = false) String observation,
                                                              @Valid @RequestBody(required = false) List<MaterialTransferItemDTO> body) throws Exception {
        List<MaterialTransferItem> ackItems = mapperUtil.mapList(body, MaterialTransferItem.class);
        MaterialTransfer out = service.confirmReceipt(id, state, observation, ackItems, receivedBy);
        return ResponseEntity.ok(mapperUtil.map(out, MaterialTransferDTO.class));
    }
}
