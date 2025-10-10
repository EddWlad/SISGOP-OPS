package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.ProjectDTO;
import com.tidsec.sisgop_backend.entity.Project;
import com.tidsec.sisgop_backend.service.IProjectService;
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
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> findAll() throws Exception {
        List<ProjectDTO> list = mapperUtil.mapList(projectService.findAll(), ProjectDTO.class);
        return ResponseEntity.ok(list);
    }
    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> findById(@PathVariable("id") String id) throws Exception {
        ProjectDTO dto = mapperUtil.map(projectService.findById(UUID.fromString(id)), ProjectDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<ProjectDTO> save(@RequestBody ProjectDTO projectDTO) throws Exception {
        Project obj = projectService.save(mapperUtil.map(projectDTO, Project.class));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdProject())
                .toUri();

        return ResponseEntity.ok(mapperUtil.map(obj, ProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(@PathVariable("id") String id, @RequestBody ProjectDTO projectDTO) throws Exception {
        Project obj = projectService.update(mapperUtil.map(projectDTO, Project.class), UUID.fromString(id));
        return ResponseEntity.ok(mapperUtil.map(obj, ProjectDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws Exception {
        projectService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<Project>> listPage(Pageable pageable) throws Exception {
        Page<Project> page = projectService.listPage(pageable);
        return ResponseEntity.ok(page);
    }
}
