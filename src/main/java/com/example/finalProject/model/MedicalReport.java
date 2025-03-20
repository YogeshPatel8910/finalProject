package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "medicalReports")
public class MedicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patientId")
    private Patient patient;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "doctorId")
    private Doctor doctor;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "appointmentId")
    @ToString.Exclude
    private Appointment appointment;

    private String symptom;

    private String diagnosis;

    private String notes;


    @OneToMany(mappedBy = "medicalReport", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Prescription> prescriptions;

//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;

}
