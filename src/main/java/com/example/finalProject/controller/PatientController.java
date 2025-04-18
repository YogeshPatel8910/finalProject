package com.example.finalProject.controller;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.MedicalReportDTO;
import com.example.finalProject.model.Branch;
import com.example.finalProject.service.AppointmentService;
import com.example.finalProject.service.MedicalReportService;
import com.example.finalProject.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling patient-related API endpoints
 * Provides functionality for patients to manage their medical reports and appointments
 */
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MedicalReportService medicalReportService;

    /**
     * Retrieves paginated medical reports for the authenticated patient
     *
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param sortBy Field to sort results by
     * @param direction Sort direction (asc/desc)
     * @param authentication Current authenticated user
     * @return Paginated medical reports with metadata
     */
    @GetMapping("/medicalReport")
    public ResponseEntity<Map<String, Object>> getMedicalReport(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            Authentication authentication) {

        Page<MedicalReportDTO> reports = medicalReportService.getMedicalReports(
                authentication.getName(), page, size, sortBy, direction, "patient");

        Map<String, Object> response = new HashMap<>();
        response.put("data", reports.getContent());
        response.put("totalElements", reports.getTotalElements());
        response.put("numberOfElements", reports.getNumberOfElements());
        response.put("pageNumber", reports.getNumber());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves current/upcoming appointments for the authenticated patient
     *
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param sortBy Field to sort results by
     * @param direction Sort direction (asc/desc)
     * @param authentication Current authenticated user
     * @return Paginated current appointments with metadata
     */
    @GetMapping("/appointment")
    public ResponseEntity<Map<String, Object>> getCurrentAppointments(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            Authentication authentication) {

        Page<AppointmentDTO> appointments = appointmentService.getCurrentAppointmentsForPatient(
                authentication.getName(), page, size, sortBy, direction);

        Map<String, Object> response = new HashMap<>();
        response.put("data", appointments.getContent());
        response.put("totalElements", appointments.getTotalElements());
        response.put("numberOfElements", appointments.getNumberOfElements());
        response.put("pageNumber", appointments.getNumber());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves appointment history for the authenticated patient
     *
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param sortBy Field to sort results by
     * @param direction Sort direction (asc/desc)
     * @param authentication Current authenticated user
     * @return Paginated appointment history with metadata
     */
    @GetMapping("/appointment/history")
    public ResponseEntity<Map<String, Object>> getAppointmentHistory(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            Authentication authentication) {

        Page<AppointmentDTO> appointments = appointmentService.getAppointmentHistoryForPatient(
                authentication.getName(), page, size, sortBy, direction);

        Map<String, Object> response = new HashMap<>();
        response.put("data", appointments.getContent());
        response.put("totalElements", appointments.getTotalElements());
        response.put("numberOfElements", appointments.getNumberOfElements());
        response.put("pageNumber", appointments.getNumber());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Creates a new appointment for the authenticated patient
     *
     * @param appointmentDTO Appointment details
     * @param authentication Current authenticated user
     * @return Created appointment data
     */
    @PostMapping("/appointment")
    public ResponseEntity<AppointmentDTO> createAppointment(
            @RequestBody AppointmentDTO appointmentDTO,
            Authentication authentication) {

        AppointmentDTO appointment = appointmentService.createAppointment(
                authentication.getName(), appointmentDTO);

        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    /**
     * Cancels an existing appointment
     *
     * @param id Appointment ID to cancel
     * @param authentication Current authenticated user
     * @return No content if successful, Not found if appointment doesn't exist
     */
    @PutMapping("/appointment/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancelAppointment(
            @PathVariable(name = "id") long id,
            Authentication authentication) {

        boolean isDeleted = appointmentService.cancelAppointment(authentication.getName(), id);

        return isDeleted
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Reschedules an existing appointment
     *
     * @param id Appointment ID to reschedule
     * @param appointmentDTO Updated appointment details
     * @param authentication Current authenticated user
     * @return No content if successful, Not found if appointment doesn't exist
     */
    @PutMapping("/appointment/{id}/reschedule")
    public ResponseEntity<AppointmentDTO> rescheduleAppointment(
            @PathVariable(name = "id") long id,
            @RequestBody AppointmentDTO appointmentDTO,
            Authentication authentication) {

        boolean isUpdated = appointmentService.rescheduleAppointment(
                authentication.getName(), id, appointmentDTO);

        return isUpdated
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieves branch data for patients
     * Used to populate dropdown lists or other UI elements
     *
     * @return List of available branches
     */
    @GetMapping("/data")
    public ResponseEntity<Object> getData() {
        List<Branch> data = patientService.getData();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}