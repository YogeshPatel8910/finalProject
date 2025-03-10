package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "department")
    private List<Branch> branch;

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<Doctor> doctors;

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<Appointment> appointments;

}
