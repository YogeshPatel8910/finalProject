package com.example.finalProject.dto;

import com.example.finalProject.model.MedicalReport;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class PrescriptionDTO {

    private Long id;

    private MedicalReport medicalReport;

    private String medicineName;

    private String dosage;

    private String duration;

    private String instructions;
}
