package com.example.finalProject.repository;

import com.example.finalProject.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    Patient findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}
