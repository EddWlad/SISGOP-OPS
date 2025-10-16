package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.MaterialDevengationDTO;
import com.tidsec.sisgop_backend.dto.MaterialDevengationItemDTO;
import com.tidsec.sisgop_backend.dto.enums.MaterialDevengationState;
import com.tidsec.sisgop_backend.entity.MaterialDevengation;
import com.tidsec.sisgop_backend.entity.MaterialDevengationItem;
import com.tidsec.sisgop_backend.service.IMaterialDevengationService;
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
@RequestMapping("/material-devengations")
public class MaterialDevengationController {
    private final IMaterialDevengationService service;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MaterialDevengationDTO>> findAll() throws Exception {
        List<MaterialDevengation> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialDevengationDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDevengationDTO> findById(@PathVariable UUID id) throws Exception {
        MaterialDevengation obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, MaterialDevengationDTO.class));
    }

    // Filtros
    @PreAuthorize("@authorizeLogic.hasAccess('findByProject')")
    @GetMapping("/by-project/{idProject}")
    public ResponseEntity<List<MaterialDevengationDTO>> byProject(@PathVariable UUID idProject) throws Exception {
        List<MaterialDevengation> list = service.findByProject(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialDevengationDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findByState')")
    @GetMapping("/by-state/{state}")
    public ResponseEntity<List<MaterialDevengationDTO>> byState(@PathVariable MaterialDevengationState state) throws Exception {
        List<MaterialDevengation> list = service.findByState(state);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialDevengationDTO.class));
    }

    // Items por cabecera
    @PreAuthorize("@authorizeLogic.hasAccess('findItems')")
    @GetMapping("/{id}/items")
    public ResponseEntity<List<MaterialDevengationItemDTO>> items(@PathVariable UUID id) throws Exception {
        List<MaterialDevengationItem> items = service.findItems(id);
        return ResponseEntity.ok(mapperUtil.mapList(items, MaterialDevengationItemDTO.class));
    }

    // Crear con items (mueve stock + kardex)
    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<MaterialDevengationDTO> save(@Valid @RequestBody MaterialDevengationDTO dto) throws Exception {
        MaterialDevengation header = mapperUtil.map(dto, MaterialDevengation.class);
        List<MaterialDevengationItem> items = dto.getItems() == null
                ? Collections.emptyList()
                : mapperUtil.mapList(dto.getItems(), MaterialDevengationItem.class);

        MaterialDevengation saved = service.createWithItems(header, items);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialDevengationDTO.class));
    }

    // Update con items (ajusta stock + kardex por delta)
    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialDevengationDTO> update(@PathVariable UUID id,
                                                         @Valid @RequestBody MaterialDevengationDTO dto) throws Exception {
        dto.setIdMaterialDevengation(id);
        MaterialDevengation header = mapperUtil.map(dto, MaterialDevengation.class);
        List<MaterialDevengationItem> items = dto.getItems() == null
                ? Collections.emptyList()
                : mapperUtil.mapList(dto.getItems(), MaterialDevengationItem.class);

        MaterialDevengation saved = service.updateWithItems(id, header, items);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialDevengationDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) throws Exception {
        boolean ok = service.delete(id); // revierte stock + kardex en la impl
        return ResponseEntity.ok(ok);
    }
}
