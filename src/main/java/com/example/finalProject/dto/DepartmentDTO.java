package com.example.finalProject.dto;

import com.example.finalProject.model.Doctor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentDTO {

    private Long id;

    private String name;

    @JsonIgnore
    private List<Doctor> doctors;
}
