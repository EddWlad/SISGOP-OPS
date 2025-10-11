package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.ProjectMaterialStockDTO;
import com.tidsec.sisgop_backend.entity.ProjectMaterialStock;
import com.tidsec.sisgop_backend.service.IProjectMaterialStockService;
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
@RequestMapping("/stock")
public class ProjectStockController {
    private final IProjectMaterialStockService service;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<ProjectMaterialStockDTO>> findAll() throws Exception {
        List<ProjectMaterialStock> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, ProjectMaterialStockDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectMaterialStockDTO> findById(@PathVariable("id") UUID id) throws Exception {
        ProjectMaterialStock obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, ProjectMaterialStockDTO.class));
    }

    // STOCK POR PROYECTO
    @PreAuthorize("@authorizeLogic.hasAccess('findByIdProject')")
    @GetMapping("/by-project/{idProject}")
    public ResponseEntity<List<ProjectMaterialStockDTO>> findByProject(@PathVariable("idProject") UUID idProject) throws Exception {
        List<ProjectMaterialStock> list = service.findByProject(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, ProjectMaterialStockDTO.class));
    }

    // PAGEABLE
    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<ProjectMaterialStockDTO>> listPageable(Pageable pageable) throws Exception {
        Page<ProjectMaterialStock> page = service.listPage(pageable);
        Page<ProjectMaterialStockDTO> pageDTO = page.map(s -> mapperUtil.map(s, ProjectMaterialStockDTO.class));
        return ResponseEntity.ok(pageDTO);
    }
}
