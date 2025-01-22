package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@DiscriminatorValue("doctors")
@Table(name = "doctors")
public class Doctor extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "branchId")
    private Branch branch;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointment;

    private String specialization;

}

