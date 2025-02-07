package com.example.finalProject.dto;

import com.example.finalProject.model.EStatus;
import com.example.finalProject.model.MedicalReport;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentDTO {

    private Long id;

    private String patientName;

    private String branchName;

    private String doctorName;

    private String departmentName;

    private String reason;

    private LocalDate date;

    private LocalTime timeSlot;

    private MedicalReport medicalReport;

    private EStatus status;

}
