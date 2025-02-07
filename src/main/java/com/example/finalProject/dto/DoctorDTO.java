package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DoctorDTO extends UserDTO{

    private Long id;

    private Branch branch;

    private Department department;

    private List<LocalDate> availableDays;

    private List<Appointment> appointment;

    private String specialization;
}
