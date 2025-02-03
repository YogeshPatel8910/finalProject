package com.example.finalProject.repository;

import com.example.finalProject.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    boolean existsByName(String name);

    Doctor findByName(String name);

    void deleteByName(String name);
}
