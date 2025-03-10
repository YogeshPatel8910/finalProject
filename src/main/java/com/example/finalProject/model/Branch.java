package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String phone;


    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Doctor> doctors;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Appointment> appointments;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name="branch_department",
            joinColumns = @JoinColumn(name = "branch_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private List<Department> department;

}
