package com.tidsec.sisgop_backend.config;

import com.tidsec.sisgop_backend.entity.Role;
import com.tidsec.sisgop_backend.entity.User;
import com.tidsec.sisgop_backend.entity.UserRole;
import com.tidsec.sisgop_backend.repository.IRoleRepository;
import com.tidsec.sisgop_backend.repository.IUserRepository;
import com.tidsec.sisgop_backend.repository.IUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Bean
    public ApplicationRunner seedRunner() {
        return args -> {
            if (!seedEnabled) return;
            seedRolesAndUsers();
        };
    }

    @Transactional
    protected void seedRolesAndUsers() {
        Role adminRole = findOrCreateRole("ADMINISTRADOR", "Rol con permisos administrativos");
        Role resiRole   = findOrCreateRole("RESIDENTE", "Rol para gestion y ejecucion de proyecto");
        Role adquiRole   = findOrCreateRole("ADQUISICIONES", "Rol para gestion de compras");
        Role contraRole   = findOrCreateRole("CONTRATISTA", "Rol para ejecucion del en campo del proyecto");

        User admin = findOrCreateUser("Administrador", "admin@ebicssa.com", "admin123");
        User resi   = findOrCreateUser("Residente", "residente@ebicssa.com", "resi123");
        User adqui   = findOrCreateUser("Adquisiciones", "adquisiones@ebicssa.com", "adqui123");
        User contra   = findOrCreateUser("Contratista", "contratista@ebicssa.com", "contra123");

        linkRoleIfMissing(admin.getIdUser(), adminRole.getIdRole());
        linkRoleIfMissing(resi.getIdUser(),   resiRole.getIdRole());
        linkRoleIfMissing(adqui.getIdUser(),   adquiRole.getIdRole());
        linkRoleIfMissing(contra.getIdUser(),   contraRole.getIdRole());
    }

    private Role findOrCreateRole(String roleName, String description) {
        Optional<Role> opt = roleRepository.findByName(roleName);
        if (opt.isPresent()) {
            return opt.get();
        }
        Role r = new Role();
        r.setName(roleName);
        r.setStatus(1);
        r.setDescription(description);
        return roleRepository.save(r);
    }

    private User findOrCreateUser(String fullName, String email, String rawPassword) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isPresent()) {
            return opt.get();
        }
        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        if (rawPassword != null && !rawPassword.isBlank()
                && !(rawPassword.startsWith("$2a$") || rawPassword.startsWith("$2b$") || rawPassword.startsWith("$2y$"))) {
            u.setPassword(passwordEncoder.encode(rawPassword));
        } else {
            u.setPassword(rawPassword);
        }
        u.setStatus(1);
        return userRepository.save(u);
    }

    private void linkRoleIfMissing(UUID userId, UUID roleId) {
        boolean exists = userRoleRepository
                .existsByUser_IdUserAndRole_IdRole(userId, roleId);
        if (!exists) {
            UserRole ur = new UserRole();
            User user = userRepository.findById(userId).orElseThrow();
            Role role = roleRepository.findById(roleId).orElseThrow();
            ur.setUser(user);
            ur.setRole(role);
            ur.setStatus(1);
            userRoleRepository.save(ur);
        }
    }
}