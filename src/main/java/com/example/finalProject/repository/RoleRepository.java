package com.example.finalProject.repository;

import com.example.finalProject.model.ERole;
import com.example.finalProject.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByName(ERole role);
}
