package com.example.finalProject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;

    private String name;
    private String address;
    private String phone;


    @OneToMany(mappedBy = "branch")
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "branch")
    private List<Appointment> appointments;
}
