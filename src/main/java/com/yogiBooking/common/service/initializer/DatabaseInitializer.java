package com.yogiBooking.common.service.initializer;

import com.yogiBooking.common.entity.Role;
import com.yogiBooking.common.entity.User;
import com.yogiBooking.common.entity.constants.Status;
import com.yogiBooking.common.repository.RoleRepository;
import com.yogiBooking.common.repository.UserRepository;
import com.yogiBooking.common.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    @Value("${default.user.email:admin@yogibooking.com}")
    private String defaultEmail;

    @Value("${default.user.password:admin}")
    private String defaultPassword;

    @Value("${default.user.role:Admin}")
    private String defaultRole;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Required for hashing passwords

    @Override
    @Transactional
    public void run(String... args) {
        initializeRoles();
        initializeDefaultUser();
    }

    private void initializeRoles() {
        insertRoleIfNotExists("Admin", "Administrator role");
        insertRoleIfNotExists("User", "Regular user role");
        insertRoleIfNotExists("Yogi", "Yogi user role");
        insertRoleIfNotExists("Guest", "Guest user role");
    }

    private void insertRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            role.setDescription(description);
            role.setStatus(Status.ACTIVE);
            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedAt(LocalDateTime.now());
            roleRepository.save(role);
            log.info("Inserted role: {}", roleName);
        } else {
            log.info("Role '{}' already exists, skipping insert.", roleName);
        }
    }

    private void initializeDefaultUser() {
        if (!userRepository.existsByEmail(defaultEmail)) {
            Role role = roleRepository.findByName(defaultRole).orElse(null);
            User user = new User();
            user.setEmail(defaultEmail);
            user.setName("Admin");
            user.setPassword(passwordEncoder.encode(defaultPassword)); // Store hashed password
            user.setStatus(Status.ACTIVE);
            user.setConfirmedAt(LocalDateTime.now());
            assert role != null;
            user.setRoleId(role.getId());
            user.setRole(role); // Assign Admin role
            userRepository.save(user);
            log.info("Inserted user: {}", JsonUtil.toPrettyJson(user));
        } else {
            log.info("Default user '{}' already exists, skipping insert.", defaultEmail);
        }
    }
}
