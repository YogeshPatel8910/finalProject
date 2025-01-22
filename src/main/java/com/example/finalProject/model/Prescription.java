package com.example.finalProject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicalReportId")
    private MedicalReport medicalReport;

    private String medicineName;

    private String dosage;

    private String duration;

    private String instructions;


//    @CreationTimestamp
//    private LocalDateTime createdAt;
}
