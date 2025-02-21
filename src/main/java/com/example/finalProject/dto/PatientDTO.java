package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.MedicalReport;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class PatientDTO extends  UserDTO{

    private Long id;

    private String address;

    private Integer age;

    private String gender;

    private List<Appointment> appointment;

    private List<MedicalReport> medicalReport;
}
