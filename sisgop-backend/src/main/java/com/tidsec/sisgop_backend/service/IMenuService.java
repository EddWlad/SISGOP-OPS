package com.tidsec.sisgop_backend.service;


import com.tidsec.sisgop_backend.entity.Menu;

import java.util.List;

public interface IMenuService {
    List<Menu> getMenusByUsername(String username);
}
