package com.tidsec.sisgop_backend.controller;


import com.tidsec.sisgop_backend.dto.MeasurementUnitDTO;
import com.tidsec.sisgop_backend.entity.MeasurementUnit;
import com.tidsec.sisgop_backend.service.IMeasurementUnitService;
import com.tidsec.sisgop_backend.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/measurement-units")
@RequiredArgsConstructor
public class MeasurementUnitController {

    private final IMeasurementUnitService measurementUnitService;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MeasurementUnitDTO>> findAll() throws Exception {
        List<MeasurementUnitDTO> list = mapperUtil.mapList(measurementUnitService.findAll(), MeasurementUnitDTO.class);
        return ResponseEntity.ok(list);
    }
    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<MeasurementUnitDTO> findById(@PathVariable("id") String id) throws Exception {
        MeasurementUnitDTO obj = mapperUtil.map(measurementUnitService.findById(UUID.fromString(id)), MeasurementUnitDTO.class);
        return ResponseEntity.ok(obj);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<MeasurementUnitDTO> save(@RequestBody MeasurementUnitDTO measurementUnitDTO) throws Exception {
        MeasurementUnit obj = measurementUnitService.save(mapperUtil.map(measurementUnitDTO, MeasurementUnit.class));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdMeasurementUnit())
                .toUri();

        return ResponseEntity.ok(mapperUtil.map(obj, MeasurementUnitDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<MeasurementUnitDTO> update(@PathVariable("id") String id, @RequestBody MeasurementUnitDTO measurementUnitDTO) throws Exception {
        MeasurementUnit obj = measurementUnitService.update(mapperUtil.map(measurementUnitDTO, MeasurementUnit.class), UUID.fromString(id));
        return ResponseEntity.ok(mapperUtil.map(obj, MeasurementUnitDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws Exception {
        measurementUnitService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<MeasurementUnit>> listPage(Pageable pageable) throws Exception {
        Page<MeasurementUnit> page = measurementUnitService.listPage(pageable);
        return ResponseEntity.ok(page);
    }

}
