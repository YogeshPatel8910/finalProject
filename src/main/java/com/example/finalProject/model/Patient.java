package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointment;

    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    private List<MedicalReport> medicalReport;

}
