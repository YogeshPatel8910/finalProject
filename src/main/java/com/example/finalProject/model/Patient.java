package com.example.finalProject.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.util.List;

@Data
@Entity
@DiscriminatorValue("patients")
@Table(name = "patients")
public class Patient extends User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private Integer age;

    private String gender;

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointment;

    @OneToMany(mappedBy = "patient")
    private List<MedicalReport> medicalReport;

}
