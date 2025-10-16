package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.ContractorDispatchConfirmDTO;
import com.tidsec.sisgop_backend.dto.ContractorDispatchConfirmItemDTO;
import com.tidsec.sisgop_backend.dto.MaterialDispatchDTO;
import com.tidsec.sisgop_backend.dto.MaterialDispatchItemDTO;
import com.tidsec.sisgop_backend.entity.MaterialDispatch;
import com.tidsec.sisgop_backend.entity.MaterialDispatchItem;
import com.tidsec.sisgop_backend.entity.User;
import com.tidsec.sisgop_backend.service.IMaterialDispatchService;
import com.tidsec.sisgop_backend.service.IUserService;
import com.tidsec.sisgop_backend.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/material-dispatches")
public class MaterialDispatchController {
    private final IMaterialDispatchService service;
    private final IUserService userService;
    private final MapperUtil mapperUtil;

    // ---------- LISTAR TODOS (status != 0 lo maneja tu genérico) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MaterialDispatchDTO>> findAll() throws Exception {
        List<MaterialDispatch> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialDispatchDTO.class));
    }

    // ---------- OBTENER POR ID ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDispatchDTO> findById(@PathVariable("id") UUID id) throws Exception {
        MaterialDispatch obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, MaterialDispatchDTO.class));
    }

    // ---------- LISTAR POR PROYECTO ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findByIdProject')")
    @GetMapping("/by-project/{idProject}")
    public ResponseEntity<List<MaterialDispatchDTO>> findByProject(@PathVariable("idProject") UUID idProject) throws Exception {
        List<MaterialDispatch> list = service.findByProject(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialDispatchDTO.class));
    }

    // ---------- LISTAR POR SOLICITUD ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findByIdRequest')")
    @GetMapping("/by-request/{idMaterialsRequest}")
    public ResponseEntity<List<MaterialDispatchDTO>> findByRequest(@PathVariable("idMaterialsRequest") UUID idMaterialsRequest) throws Exception {
        List<MaterialDispatch> list = service.findByRequest(idMaterialsRequest);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialDispatchDTO.class));
    }

    // ---------- LISTAR ITEMS DE UN DESPACHO ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findItems')")
    @GetMapping("/{id}/items")
    public ResponseEntity<List<MaterialDispatchItemDTO>> findItems(@PathVariable("id") UUID id) throws Exception {
        List<MaterialDispatchItem> items = service.findItems(id);
        return ResponseEntity.ok(mapperUtil.mapList(items, MaterialDispatchItemDTO.class));
    }

    // ---------- CREAR DESPACHO (CABECERA + ITEMS) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<MaterialDispatchDTO> save(@Valid @RequestBody MaterialDispatchDTO dto) throws Exception {
        MaterialDispatch header = mapperUtil.map(dto, MaterialDispatch.class);
        List<MaterialDispatchItem> items = (dto.getItems() == null)
                ? Collections.emptyList()
                : mapperUtil.mapList(dto.getItems(), MaterialDispatchItem.class);

        MaterialDispatch saved = service.createWithItems(header, items);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialDispatchDTO.class));
    }

    // ---------- ELIMINAR (soft delete: status = 0, lo maneja tu genérico) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- PAGEABLE ----------
    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<MaterialDispatchDTO>> listPageable(Pageable pageable) throws Exception {
        Page<MaterialDispatch> page = service.listPage(pageable);
        Page<MaterialDispatchDTO> pageDTO = page.map(d -> mapperUtil.map(d, MaterialDispatchDTO.class));
        return ResponseEntity.ok(pageDTO);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialDispatchDTO> update(@PathVariable("id") UUID id,
                                                      @Valid @RequestBody MaterialDispatchDTO dto) throws Exception {
        dto.setIdMaterialDispatch(id);
        MaterialDispatch header = mapperUtil.map(dto, MaterialDispatch.class);
        List<MaterialDispatchItem> items = (dto.getItems() == null)
                ? List.of()
                : mapperUtil.mapList(dto.getItems(), MaterialDispatchItem.class);

        MaterialDispatch saved = service.updateWithItems(header, items);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialDispatchDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('contractorConfirm')")
    @PostMapping("/{id}/contractor-confirm")
    public ResponseEntity<MaterialDispatchDTO> contractorConfirm(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ContractorDispatchConfirmDTO body) throws Exception {

        // Mapear ítems SOLO con campos del contratista (trazabilidad)
        List<MaterialDispatchItem> ackItems = (body.getItems() == null ? List.<ContractorDispatchConfirmItemDTO>of() : body.getItems())
                .stream()
                .map(it -> {
                    MaterialDispatchItem e = new MaterialDispatchItem();
                    e.setMaterial(it.getMaterial());                      // {"idMaterial": "..."}
                    e.setContractorChecked(it.getChecked());              // check
                    e.setContractorObservation(it.getObservation());      // novedad por ítem
                    e.setQuantityReceived(it.getQuantityReceived());      // opcional
                    return e;
                })
                .toList();

        // id del usuario autenticado (si ya lo pasas desde tu seguridad, úsalo aquí)
        UUID idUserLogged = currentUserId();

        MaterialDispatch saved = service.confirmReceipt(
                id,
                body.getReceiptState(),            // RECIBIDO | RECIBIDO_NOVEDADES
                body.getObservation(),             // observación general
                ackItems,                          // trazabilidad por ítem
                idUserLogged                       // quien confirma
        );

        return ResponseEntity.ok(mapperUtil.map(saved, MaterialDispatchDTO.class));
    }

    private UUID currentUserId() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof User u) {
            return u.getIdUser();
        }
        return currentUserIdByUsername(auth.getName());
    }

    private UUID currentUserIdByUsername(String usernameOrEmail) throws Exception {
        return userService.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usernameOrEmail))
                .getIdUser();
    }
}
