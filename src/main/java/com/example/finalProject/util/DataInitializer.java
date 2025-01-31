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

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Insert default data into the database
        if(roleRepository.count()==0) {
            List<ERole> roles = new ArrayList<>(Arrays.asList(ERole.ROLE_ADMIN, ERole.ROLE_DOCTOR, ERole.ROLE_PATIENT));
            List<Role> add = roles.stream()
                    .map(roleEnum -> {
                        Role role = new Role();
                        role.setName(roleEnum); // Assuming setName accepts ERole
                        return role;
                    }).toList();
            roleRepository.saveAll(add);
        }
    }
}