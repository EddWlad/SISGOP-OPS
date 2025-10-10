package com.tidsec.sisgop_backend.controller;
import com.tidsec.sisgop_backend.dto.MaterialSupplierDTO;
import com.tidsec.sisgop_backend.entity.MaterialSupplier;
import com.tidsec.sisgop_backend.service.IMaterialSupplierService;
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
@RequestMapping("/material-suppliers")
public class MaterialSupplierController {

    private final IMaterialSupplierService service;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MaterialSupplierDTO>> findAll() throws Exception {
        List<MaterialSupplier> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialSupplierDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialSupplierDTO> findById(@PathVariable("id") UUID id) throws Exception {
        MaterialSupplier obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, MaterialSupplierDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findByIdMaterial')")
    @GetMapping("/by-material/{idMaterial}")
    public ResponseEntity<List<MaterialSupplierDTO>> findByMaterial(@PathVariable("idMaterial") UUID idMaterial) throws Exception {
        List<MaterialSupplier> list = service.findByMaterial(idMaterial);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialSupplierDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findByIdSupplier')")
    @GetMapping("/by-supplier/{idSupplier}")
    public ResponseEntity<List<MaterialSupplierDTO>> findBySupplier(@PathVariable("idSupplier") UUID idSupplier) throws Exception {
        List<MaterialSupplier> list = service.findBySupplier(idSupplier);
        return ResponseEntity.ok(mapperUtil.mapList(list, MaterialSupplierDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<MaterialSupplierDTO> save(@Valid @RequestBody MaterialSupplierDTO dto) throws Exception {
        MaterialSupplier entity = mapperUtil.map(dto, MaterialSupplier.class);
        MaterialSupplier saved = service.upsert(entity);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialSupplierDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialSupplierDTO> update(@PathVariable("id") UUID id,
                                                      @Valid @RequestBody MaterialSupplierDTO dto) throws Exception {
        dto.setIdMaterialSupplier(id);
        MaterialSupplier entity = mapperUtil.map(dto, MaterialSupplier.class);
        MaterialSupplier saved = service.upsert(entity);
        return ResponseEntity.ok(mapperUtil.map(saved, MaterialSupplierDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<MaterialSupplierDTO>> listPageable(Pageable pageable) throws Exception {
        Page<MaterialSupplier> page = service.listPage(pageable);
        Page<MaterialSupplierDTO> pageDTO = page.map(ms -> mapperUtil.map(ms, MaterialSupplierDTO.class));
        return ResponseEntity.ok(pageDTO);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('batch')")
    @PostMapping("/batch")
    public ResponseEntity<List<MaterialSupplierDTO>> saveBatch(@Valid @RequestBody List<MaterialSupplierDTO> body) throws Exception {
        List<MaterialSupplier> entities = mapperUtil.mapList(body, MaterialSupplier.class);
        List<MaterialSupplier> saved = service.upsertBatch(entities);
        return ResponseEntity.ok(mapperUtil.mapList(saved, MaterialSupplierDTO.class));
    }
}