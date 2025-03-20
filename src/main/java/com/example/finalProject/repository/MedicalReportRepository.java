package com.example.finalProject.repository;

import com.example.finalProject.model.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport,Long> {
}
