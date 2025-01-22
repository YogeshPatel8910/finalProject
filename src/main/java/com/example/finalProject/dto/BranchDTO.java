package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.Doctor;
import lombok.Data;

import java.util.List;

@Data
public class BranchDTO {

    private Long branchId;

    private String name;

    private String address;

    private String phone;

    private List<Doctor> doctors;

    private List<Appointment> appointments;
}
