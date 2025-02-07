package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Entity
@DiscriminatorValue("doctors")
@Table(name = "doctors")
public class Doctor extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branchId")
    private Branch branch;


    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;

    private Set<LocalDate> availableDays;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointment;

    private String specialization;

}

