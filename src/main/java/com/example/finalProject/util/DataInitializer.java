package com.example.finalProject.util;

import com.example.finalProject.model.ERole;
import com.example.finalProject.model.Role;
import com.example.finalProject.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Component responsible for initializing default roles in the database at application startup.
 * Implements CommandLineRunner to execute initialization logic when the application starts.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    // Final field for role repository - best practice for dependency injection
    private final RoleRepository roleRepository;

    /**
     * Constructor injection is preferred over field injection
     * @param roleRepository Repository for Role entities
     */
    @Autowired // This annotation is optional with constructor injection in newer Spring versions
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Method executed at application startup to initialize default roles
     * @param args Command line arguments (not used)
     * @throws Exception If initialization fails
     */
    @Override
    public void run(String... args) throws Exception {
        // Only initialize roles if the database is empty
        if(roleRepository.count() == 0) {
            // Create list of all role enums
            List<ERole> roles = new ArrayList<>(Arrays.asList(ERole.ROLE_ADMIN, ERole.ROLE_DOCTOR, ERole.ROLE_PATIENT));

            // Convert role enums to Role entities
            List<Role> rolesToAdd = roles.stream()
                    .map(roleEnum -> {
                        Role role = new Role();
                        role.setName(roleEnum);
                        return role;
                    }).toList();

            // Save all roles at once for better performance
            roleRepository.saveAll(rolesToAdd);
        }
    }
}