package com.example.finalProject.dto;

import com.example.finalProject.model.MedicalReport;
import lombok.Data;

@Data
public class PrescriptionDTO {

    private Long id;

    private MedicalReport medicalReport;

    private String name;

    private String dosage;

    private String duration;

    private String instructions;
}
