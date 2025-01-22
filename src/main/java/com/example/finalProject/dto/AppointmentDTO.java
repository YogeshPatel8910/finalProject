package com.example.finalProject.dto;

import com.example.finalProject.model.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentDTO {

    private Long id;

    private Patient patient;

    private Branch branch;

    private Doctor doctor;

    private String reason;

    private LocalDate date;

    private LocalTime timeSlot;

    private MedicalReport medicalReport;

    private EStatus status;

}
