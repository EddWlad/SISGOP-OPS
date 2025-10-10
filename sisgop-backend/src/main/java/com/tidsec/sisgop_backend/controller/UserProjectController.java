package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.UserProjectDTO;
import com.tidsec.sisgop_backend.entity.UserProject;
import com.tidsec.sisgop_backend.service.IUserProjectService;
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
@RequestMapping("/user-projects")
public class UserProjectController {
    private final IUserProjectService service;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<UserProjectDTO>> findAll() throws Exception {
        List<UserProject> list = service.findAll();
        return ResponseEntity.ok(mapperUtil.mapList(list, UserProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<UserProjectDTO> findById(@PathVariable("id") UUID id) throws Exception {
        UserProject obj = service.findById(id);
        return ResponseEntity.ok(mapperUtil.map(obj, UserProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findByIdProject')")
    @GetMapping("/by-project/{idProject}")
    public ResponseEntity<List<UserProjectDTO>> findByProject(@PathVariable("idProject") UUID idProject) throws Exception {
        List<UserProject> list = service.findByProject(idProject);
        return ResponseEntity.ok(mapperUtil.mapList(list, UserProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findByIdUser')")
    @GetMapping("/by-user/{idUser}")
    public ResponseEntity<List<UserProjectDTO>> findByUser(@PathVariable("idUser") UUID idUser) throws Exception {
        List<UserProject> list = service.findByUser(idUser);
        return ResponseEntity.ok(mapperUtil.mapList(list, UserProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<UserProjectDTO> save(@Valid @RequestBody UserProjectDTO dto) throws Exception {
        UserProject entity = mapperUtil.map(dto, UserProject.class);
        UserProject saved = service.upsert(entity);
        return ResponseEntity.ok(mapperUtil.map(saved, UserProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<UserProjectDTO> update(@PathVariable("id") UUID id,
                                                 @Valid @RequestBody UserProjectDTO dto) throws Exception {
        dto.setIdUserProject(id);
        UserProject entity = mapperUtil.map(dto, UserProject.class);
        UserProject saved = service.upsert(entity);
        return ResponseEntity.ok(mapperUtil.map(saved, UserProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<UserProjectDTO>> listPageable(Pageable pageable) throws Exception {
        Page<UserProject> page = service.listPage(pageable);
        Page<UserProjectDTO> pageDTO = page.map(up -> mapperUtil.map(up, UserProjectDTO.class));
        return ResponseEntity.ok(pageDTO);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('batch')")
    @PostMapping("/batch")
    public ResponseEntity<List<UserProjectDTO>> saveBatch(@Valid @RequestBody List<UserProjectDTO> body) throws Exception {
        List<UserProject> entities = mapperUtil.mapList(body, UserProject.class);
        List<UserProject> saved = service.upsertBatch(entities);
        return ResponseEntity.ok(mapperUtil.mapList(saved, UserProjectDTO.class));
    }
}
