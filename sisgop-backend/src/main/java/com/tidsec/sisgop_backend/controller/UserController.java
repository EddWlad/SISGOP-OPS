package com.tidsec.sisgop_backend.controller;

import com.tidsec.sisgop_backend.dto.RoleDTO;
import com.tidsec.sisgop_backend.dto.UserDTO;
import com.tidsec.sisgop_backend.dto.UserDetailDTO;
import com.tidsec.sisgop_backend.dto.UserListRoleDTO;
import com.tidsec.sisgop_backend.entity.Role;
import com.tidsec.sisgop_backend.entity.User;
import com.tidsec.sisgop_backend.repository.IUserRoleRepository;
import com.tidsec.sisgop_backend.service.IUserService;
import com.tidsec.sisgop_backend.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final MapperUtil mapperUtil;
    private final IUserRoleRepository urRepo;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() throws Exception {
        List<UserDTO> list = mapperUtil.mapList(userService.findAll(), UserDTO.class);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findUserDetail')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDTO> findUserDetail(@PathVariable("id") String id) throws Exception{
        UUID userId = UUID.fromString(id);
        User user = userService.findUserDetail(userId);

        UserDetailDTO detail = new UserDetailDTO();
        detail.setUser(mapperUtil.map(user, UserDTO.class));

        List<RoleDTO> roles = urRepo.findActiveRolesByUserId(userId).stream()
                .map(ur -> mapperUtil.map(ur.getRole(), RoleDTO.class))
                .toList();
        detail.setRoles(roles);

        return ResponseEntity.ok(detail);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<UserListRoleDTO> save(@RequestBody UserListRoleDTO dto) throws Exception{
        User obj1 = mapperUtil.map(dto.getUser(), User.class);
        List<Role> list = mapperUtil.mapList(dto.getRoles(), Role.class);

        User obj = userService.saveTransactional(obj1, list);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdUser())
                .toUri();
        return ResponseEntity.ok(mapperUtil.map(obj, UserListRoleDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<UserListRoleDTO> update(@PathVariable("id") String id, @RequestBody UserListRoleDTO dto) throws Exception{
        User user = mapperUtil.map(dto.getUser(), User.class);
        List<Role> roles = mapperUtil.mapList(dto.getRoles(), Role.class);

        User obj = userService.updateTransactional(UUID.fromString(id), user, roles);

        return ResponseEntity.ok(mapperUtil.map(obj, UserListRoleDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws Exception{
        userService.softDeleteTransactional(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findActiveAdmins')")
    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> findActiveAdmins() throws Exception {
        List<User> list = userService.findActiveUsersByRole("ADMINISTRADOR");
        return ResponseEntity.ok(mapperUtil.mapList(list, UserDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findActiveContractors')")
    @GetMapping("/contractors")
    public ResponseEntity<List<UserDTO>> findActiveContractors() throws Exception {
        List<User> list = userService.findActiveUsersByRole("CONTRATISTA");
        return ResponseEntity.ok(mapperUtil.mapList(list, UserDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findActiveResidents')")
    @GetMapping("/residents")
    public ResponseEntity<List<UserDTO>> findActiveResidents() throws Exception {
        List<User> list = userService.findActiveUsersByRole("RESIDENTE");
        return ResponseEntity.ok(mapperUtil.mapList(list, UserDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findActiveAcquisitions')")
    @GetMapping("/acquisitions")
    public ResponseEntity<List<UserDTO>> findActiveAcquisitions() throws Exception {
        List<User> list = userService.findActiveUsersByRole("ADQUISICIONES");
        return ResponseEntity.ok(mapperUtil.mapList(list, UserDTO.class));
    }
}
