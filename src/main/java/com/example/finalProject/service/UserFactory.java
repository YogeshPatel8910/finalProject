package com.example.finalProject.service;

import com.example.finalProject.model.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    @Autowired
    @Qualifier("adminService")
    private AdminService adminService;

    @Autowired
    @Qualifier("doctorService")
    private DoctorService doctorService;

    @Autowired
    @Qualifier("patientService")
    private PatientService patientService;


    public UserService getService(ERole role) {
        return switch (role) {
            case ROLE_ADMIN -> adminService;
            case ROLE_DOCTOR -> doctorService;
            case ROLE_PATIENT -> patientService;
        };
    }
}