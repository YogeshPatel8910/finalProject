package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class BranchDTO {

    private Long id;

    private String name;

    private String address;

    private String phone;

//    @JsonIgnore
    private List<String> departmentName;

    @JsonIgnore
    private List<String> doctorsName;

    @JsonIgnore
    private List<Appointment> appointments;
}
