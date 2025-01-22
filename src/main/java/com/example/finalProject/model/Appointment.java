package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "appointments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctorId", "date", "timeSlot"})})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patientId")
    private Patient patient;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "branchId")
    private Branch branch;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "doctorId")
    private Doctor doctor;

    private String reason;

    private LocalDate date;

    private LocalTime timeSlot;

    @JsonIgnore
    @OneToOne(mappedBy = "appointment")
    @PrimaryKeyJoinColumn
    private MedicalReport medicalReport;

    @Enumerated
    private EStatus status;

//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;

}
