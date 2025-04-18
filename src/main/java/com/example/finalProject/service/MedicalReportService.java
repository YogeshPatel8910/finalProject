package com.example.finalProject.service;

import com.example.finalProject.dto.MedicalReportDTO;
import com.example.finalProject.model.Activity;
import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.EStatus;
import com.example.finalProject.model.MedicalReport;
import com.example.finalProject.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Service handling medical report operations
 * Manages creation and retrieval of medical reports
 */
@Service
public class MedicalReportService {

    @Autowired
    private MedicalReportRepository medicalReportRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ActivityRepsoitory activityRepsoitory;

    @Autowired
    private UserRepository userRepository;

    // ModelMapper for DTO to entity conversion and vice versa
    ModelMapper mapper = new ModelMapper();

    /**
     * Creates a new medical report
     *
     * @param id ID for reference
     * @param name User name who created the report
     * @param medicalReportDTO DTO containing report data
     * @return The created medical report as DTO
     */
    @Transactional
    public MedicalReportDTO createReport(long id, String name, MedicalReportDTO medicalReportDTO) {
        // Find the associated appointment
        Appointment appointment = appointmentRepository.findById(medicalReportDTO.getAppointmentId()).orElse(null);
        assert appointment != null;

        // Create and populate the medical report
        MedicalReport medicalReport = new MedicalReport();
        mapper.map(medicalReportDTO, medicalReport);
        medicalReport.setAppointment(appointment);
        medicalReport.setPatient(appointment.getPatient());
        medicalReport.setDoctor(appointment.getDoctor());

        // Set the medical report reference for each prescription
        medicalReport.getPrescriptions().forEach(prescription -> prescription.setMedicalReport(medicalReport));
        medicalReport.setCreatedAt(LocalDateTime.now());

        // Save the report
        MedicalReport newReport = medicalReportRepository.save(medicalReport);
        System.out.println("123456: " + newReport);

        // Create activity log entry
        Activity activity = new Activity();
        activity.setAction("Medical Report Created");
        activity.setStatus("Created");
        activity.setName(medicalReport.getPatient().getName());
        activity.setUser(userRepository.findByName(medicalReport.getDoctor().getName()).orElse(null));
        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);

        return mapper.map(newReport, MedicalReportDTO.class);
    }

    /**
     * Retrieves medical reports with pagination and sorting
     *
     * @param name Name of doctor or patient
     * @param page Page number
     * @param size Page size
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @param role Role (patient/doctor)
     * @return Page of medical report DTOs
     */
    public Page<MedicalReportDTO> getMedicalReports(String name, int page, int size, String sortBy, String direction, String role) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Status filter (not used in current implementation)
        List<EStatus> status = Arrays.asList(EStatus.PENDING, EStatus.CONFIRMED);

        // Return reports based on role (patient or doctor)
        if (role == "patient")
            return medicalReportRepository.findAllByPatientName(name, pageable)
                    .map(medicalReport -> mapper.map(medicalReport, MedicalReportDTO.class));
        else
            return medicalReportRepository.findAllByDoctorName(name, pageable)
                    .map(medicalReport -> mapper.map(medicalReport, MedicalReportDTO.class));
    }
}