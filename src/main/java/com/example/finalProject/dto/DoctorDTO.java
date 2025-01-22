package com.example.finalProject.dto;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
public class DoctorDTO {

    private Long id;

    private Branch branch;

    private Department department;

    private List<Appointment> appointment;

    private String specialization;
}
