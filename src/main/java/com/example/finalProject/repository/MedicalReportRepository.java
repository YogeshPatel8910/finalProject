package com.example.finalProject.repository;

import com.example.finalProject.model.MedicalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport,Long> {
    Page<MedicalReport> findAllByPatientName(String name, Pageable pageable);

    Page<MedicalReport> findAllByDoctorName(String name, Pageable pageable);
}
