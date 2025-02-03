package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.model.Patient;
import com.example.finalProject.model.Prescription;
import lombok.Data;

import java.util.List;

@Data
public class MedicalReportDTO {

    private Long id;

    private Patient patient;

    private Doctor doctor;

    private Appointment appointment;

    private String symptom;

    private String diagnosis;

    private String notes;

    private List<Prescription> prescriptions;

}
