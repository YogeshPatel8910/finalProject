package com.example.finalProject.controller;

import com.example.finalProject.model.*;
import com.example.finalProject.repository.UserRepository;
import com.example.finalProject.service.DoctorService;
import com.example.finalProject.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller providing publicly accessible data endpoints.
 * Supplies data for registration forms, appointment scheduling, dashboard metrics, and doctor leave dates.
 * All endpoints are accessible without authentication via the 'auth' path.
 */
@RestController
@RequestMapping("api/auth/data")
public class DataController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves data needed for user registration forms.
     * Returns a mapping of branches to their departments.
     *
     * @return Map with branch names as keys and lists of department names as values
     */
    @GetMapping("/register")
    public ResponseEntity<Object> getRegisterData() {
        List<Branch> data = patientService.getData();
        Map<String, List<String>> response = data.stream()
                .collect(Collectors.toMap(
                        Branch::getName,
                        hospital -> hospital.getDepartment().stream()
                                .map(Department::getName)
                                .collect(Collectors.toList())
                ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves data needed for appointment scheduling.
     * Creates a nested mapping structure: Branch → Department → Doctor → Date → Time slots
     *
     * @return Complex nested map structure with available appointment slots
     */
    @GetMapping("/appointment")
    public ResponseEntity<Object> getAppointmentData() {
        List<Branch> data = patientService.getData();
        Map<String, Map<String, Map<String, Map<LocalDate, List<LocalTime>>>>> response = data.stream()
                .collect(Collectors.toMap(
                        Branch::getName,  // Branch Name as Key
                        branch -> branch.getDepartment().stream().collect(Collectors.toMap(
                                Department::getName,  // Department Name as Key
                                department -> department.getDoctors().stream().collect(Collectors.toMap(
                                        Doctor::getName,  // Doctor Name as Key
                                        doctor -> doctor.getAppointment().stream()
                                                .collect(Collectors.groupingBy(
                                                        Appointment::getDate,  // Group by Date
                                                        Collectors.mapping(
                                                                Appointment::getTimeSlot, // Collect Time Slots for the date
                                                                Collectors.toList()
                                                        )
                                                ))
                                ))
                        ))
                ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves summary data for the admin dashboard.
     * Counts patients, doctors, and today's appointments.
     *
     * @return Map with counts of patients, doctors, and today's appointments
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Integer>> getDashboardData() {
        List<User> data = userRepository.findAll();
        Map<String, Integer> response = new HashMap<>();

        // Count of patients
        response.put("patient", (int) data.stream()
                .filter(user -> user instanceof Patient)
                .count());

        // Count of doctors
        response.put("doctor", (int) data.stream()
                .filter(user -> user instanceof Doctor)
                .count());

        // Count of today's appointments
        response.put("appointment", (int) data.stream()
                .filter(user -> user instanceof Doctor)
                .mapToLong(doc -> ((Doctor) doc).getAppointment().stream()
                        .filter(appointment -> appointment.getDate().equals(LocalDate.now()))
                        .count())
                .sum());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves leave dates for a specific doctor.
     *
     * @param name The name of the doctor
     * @return Set of dates when the doctor is on leave
     */
    @GetMapping("/leave/{name}")
    public ResponseEntity<Set<LocalDate>> getLeave(@PathVariable(name = "name") String name) {
        Set<LocalDate> leave = doctorService.getDate(name);
        return new ResponseEntity<>(leave, HttpStatus.OK);
    }
}