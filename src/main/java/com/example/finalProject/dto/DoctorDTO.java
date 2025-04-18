package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DoctorDTO extends UserDTO{

    private Long id;

    private String branchName;

    private String departmentName;

    private List<LocalDate> availableDays;

    private List<Appointment> appointment;

    private String specialization;
}
