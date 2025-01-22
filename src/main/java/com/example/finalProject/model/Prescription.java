package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
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
