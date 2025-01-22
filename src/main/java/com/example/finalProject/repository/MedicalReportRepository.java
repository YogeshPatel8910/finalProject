package com.example.finalProject.repository;

import com.example.finalProject.model.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalReportRepository extends JpaRepository<MedicalReport,Long> {
}
