package com.prueba_tecnica.prestamos.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.prueba_tecnica.prestamos.model.Role;
import com.prueba_tecnica.prestamos.model.User;
import com.prueba_tecnica.prestamos.repository.RoleRepository;
import com.prueba_tecnica.prestamos.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    return roleRepository.save(role);
                });

        if (!userRepository.existsByEmail("usuario@test.com")) {
            User user = new User();
            user.setEmail("usuario@test.com");
            user.setPassword(passwordEncoder.encode("123"));
            user.setRoles(Set.of(roleUser));
            userRepository.save(user);
        }

        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setRoles(Set.of(roleAdmin));
            userRepository.save(admin);
        }
    }
}
