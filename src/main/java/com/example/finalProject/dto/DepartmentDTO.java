package com.example.finalProject.dto;

import com.example.finalProject.model.Doctor;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentDTO {

    private Long id;

    private String name;

    private List<Doctor> doctors;
}
