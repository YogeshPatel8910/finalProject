package com.example.finalProject.service;

import com.example.finalProject.model.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Factory class for obtaining the appropriate UserService implementation
 * Uses the Factory design pattern to return service based on role
 */
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

    /**
     * Returns the appropriate UserService implementation based on role
     *
     * @param role The role enum value
     * @return The corresponding UserService implementation
     */
    public UserService getService(ERole role) {
        return switch (role) {
            case ROLE_ADMIN -> adminService;
            case ROLE_DOCTOR -> doctorService;
            case ROLE_PATIENT -> patientService;
        };
    }
}