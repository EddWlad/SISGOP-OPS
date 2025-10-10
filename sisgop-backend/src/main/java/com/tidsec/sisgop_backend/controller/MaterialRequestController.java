package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.DetailRequestDTO;
import com.tidsec.sisgop_backend.dto.MaterialRequestDTO;
import com.tidsec.sisgop_backend.dto.enums.RequestMaterialStatus;
import com.tidsec.sisgop_backend.entity.DetailRequest;
import com.tidsec.sisgop_backend.entity.MaterialRequest;
import com.tidsec.sisgop_backend.service.IMaterialRequestService;
import com.tidsec.sisgop_backend.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materials-requests")
public class MaterialRequestController {
    private final IMaterialRequestService service;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MaterialRequestDTO>> findAll() throws Exception {
        List<MaterialRequest> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialRequestDTO.class));
    }

    // ---------- OBTENER POR ID ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialRequestDTO> findById(@PathVariable("id") UUID id) throws Exception {
        MaterialRequest obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, MaterialRequestDTO.class));
    }

    // ---------- LISTAR POR PROYECTO ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findByIdProject')")
    @GetMapping("/by-project/{idProject}")
    public ResponseEntity<List<MaterialRequestDTO>> findByProject(@PathVariable("idProject") UUID idProject) throws Exception {
        List<MaterialRequest> list = service.findByProject(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialRequestDTO.class));
    }

    // ---------- LISTAR POR USUARIO (solicitante) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findByIdUser')")
    @GetMapping("/by-user/{idUser}")
    public ResponseEntity<List<MaterialRequestDTO>> findByUser(@PathVariable("idUser") UUID idUser) throws Exception {
        List<MaterialRequest> list = service.findByUser(idUser);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialRequestDTO.class));
    }

    // ---------- LISTAR POR ESTADO (BORRADOR, ENVIADA, ...) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findByStatus')")
    @GetMapping("/by-status/{statusRequest}")
    public ResponseEntity<List<MaterialRequestDTO>> findByStatus(@PathVariable("statusRequest") String statusRequest) throws Exception {
        RequestMaterialStatus estado = RequestMaterialStatus.valueOf(statusRequest.toUpperCase(Locale.ROOT));
        List<MaterialRequest> list = service.findByStatusRequest(estado);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialRequestDTO.class));
    }

    // ---------- GUARDAR (CABECERA + DETALLES) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<MaterialRequestDTO> save(@Valid @RequestBody MaterialRequestDTO dto) throws Exception {
        MaterialRequest header = mapperUtil.map(dto, MaterialRequest.class);
        List<DetailRequest> details = (dto.getItems() == null)
                ? Collections.emptyList()
                : mapperUtil.mapList(dto.getItems(), DetailRequest.class);

        MaterialRequest saved = service.saveWithDetails(header, details);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialRequestDTO.class));
    }

    // ---------- ACTUALIZAR (CABECERA + DETALLES) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialRequestDTO> update(@PathVariable("id") UUID id,
                                                     @Valid @RequestBody MaterialRequestDTO dto) throws Exception {
        dto.setIdMaterialsRequest(id);

        MaterialRequest header = mapperUtil.map(dto, MaterialRequest.class);
        List<DetailRequest> details = (dto.getItems() == null)
                ? Collections.emptyList()
                : mapperUtil.mapList(dto.getItems(), DetailRequest.class);

        MaterialRequest saved = service.updateWithDetails(header, details);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialRequestDTO.class));
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
    public ResponseEntity<Page<MaterialRequestDTO>> listPageable(Pageable pageable) throws Exception {
        Page<MaterialRequest> page = service.listPage(pageable);
        Page<MaterialRequestDTO> pageDTO = page.map(mr -> mapperUtil.map(mr, MaterialRequestDTO.class));
        return ResponseEntity.ok(pageDTO);
    }

    // ---------- ENVIAR SOLICITUD (BORRADOR -> ENVIADA) ----------
    @PreAuthorize("@authorizeLogic.hasAccess('send')")
    @PostMapping("/{id}/send")
    public ResponseEntity<MaterialRequestDTO> send(@PathVariable("id") UUID id) throws Exception {
        MaterialRequest sent = service.send(id);
        return ResponseEntity.ok(mapperUtil.map(sent, MaterialRequestDTO.class));
    }

    // ---------- LISTAR DETALLES DE UNA SOLICITUD ----------
    @PreAuthorize("@authorizeLogic.hasAccess('findDetails')")
    @GetMapping("/{id}/details")
    public ResponseEntity<List<DetailRequestDTO>> findDetails(@PathVariable("id") UUID id) throws Exception {
        var details = service.findDetails(id);
        return ResponseEntity.ok(mapperUtil.mapList(details, DetailRequestDTO.class));
    }
}
