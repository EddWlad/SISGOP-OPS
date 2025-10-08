package com.tidsec.sisgop_backend.service;

import com.tidsec.sisgop_backend.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IRoleService extends IGenericService<Role, UUID> {
    Page<Role> listPage(Pageable pageable);
}
