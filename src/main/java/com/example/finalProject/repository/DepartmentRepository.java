package com.example.finalProject.repository;

import com.example.finalProject.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    Department findByName(String name);

    Boolean existsByName(String name);
}
