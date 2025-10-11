package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.StockMovementDTO;
import com.tidsec.sisgop_backend.entity.StockMovement;
import com.tidsec.sisgop_backend.service.IStockMovementService;
import com.tidsec.sisgop_backend.util.MapperUtil;
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
@RequestMapping("/stock-movements")
public class StockMovementController {
    private final IStockMovementService service;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<StockMovementDTO>> findAll() throws Exception {
        List<StockMovement> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, StockMovementDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<StockMovementDTO> findById(@PathVariable("id") UUID id) throws Exception {
        StockMovement obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, StockMovementDTO.class));
    }

    // KARDEX POR PROYECTO
    @PreAuthorize("@authorizeLogic.hasAccess('findByIdProject')")
    @GetMapping("/by-project/{idProject}")
    public ResponseEntity<List<StockMovementDTO>> findByProject(@PathVariable("idProject") UUID idProject) throws Exception {
        List<StockMovement> list = service.findByProject(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, StockMovementDTO.class));
    }

    // KARDEX POR PROYECTO + MATERIAL
    @PreAuthorize("@authorizeLogic.hasAccess('findByIdProjectAndIdMaterial')")
    @GetMapping("/by-project/{idProject}/material/{idMaterial}")
    public ResponseEntity<List<StockMovementDTO>> findByProjectAndMaterial(@PathVariable("idProject") UUID idProject,
                                                                           @PathVariable("idMaterial") UUID idMaterial) throws Exception {
        List<StockMovement> list = service.findByProjectAndMaterial(idProject, idMaterial);
        return ResponseEntity.ok(mapperUtil.mapList(list, StockMovementDTO.class));
    }

    // PAGEABLE
    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<StockMovementDTO>> listPageable(Pageable pageable) throws Exception {
        Page<StockMovement> page = service.listPage(pageable);
        Page<StockMovementDTO> pageDTO = page.map(m -> mapperUtil.map(m, StockMovementDTO.class));
        return ResponseEntity.ok(pageDTO);
    }
}
