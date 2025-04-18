package com.example.finalProject.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MedicalReportDTO {

    private Long id;

    private String patientName;

    private String doctorName;

    private Long appointmentId;

    private String symptom;

    private String diagnosis;

    private String notes;

    private List<PrescriptionDTO> prescriptions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
