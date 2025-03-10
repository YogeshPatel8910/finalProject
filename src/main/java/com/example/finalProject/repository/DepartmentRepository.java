package com.example.finalProject.repository;

import com.example.finalProject.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    Optional<Department> findByName(String name);

    Boolean existsByName(String name);
}
