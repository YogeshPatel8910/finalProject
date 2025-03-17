package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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
    @ToString.Exclude
    private List<Appointment> appointment;

    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    @ToString.Exclude
    private List<MedicalReport> medicalReport;

}
