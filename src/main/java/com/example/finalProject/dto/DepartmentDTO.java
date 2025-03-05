package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.Doctor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentDTO {

    private Long id;

    private String name;

    private String branchName;

    @JsonIgnore
    private List<Doctor> doctors;

    @JsonIgnore
    private List<Appointment> appointments;
}
