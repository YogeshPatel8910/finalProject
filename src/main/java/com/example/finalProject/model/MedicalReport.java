package com.example.finalProject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "medicalReports")
public class MedicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patientId")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctorId")
    private Doctor doctor;

    @OneToOne
    @MapsId
    @JoinColumn(name = "appointmentId")
    private Appointment appointment;

    private String symptom;

    private String diagnosis;

    private String notes;

    @OneToMany(mappedBy = "medicalReport")
    private List<Prescription> prescriptions;

//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;

}
