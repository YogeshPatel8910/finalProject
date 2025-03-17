package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "medicalReportId")
    private MedicalReport medicalReport;

    private String name;

    private String dosage;

    private String duration;

    private String instructions;


//    @CreationTimestamp
//    private LocalDateTime createdAt;
}
