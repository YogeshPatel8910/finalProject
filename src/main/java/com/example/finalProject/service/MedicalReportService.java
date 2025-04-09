package com.example.finalProject.service;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.MedicalReportDTO;
import com.example.finalProject.dto.PrescriptionDTO;
import com.example.finalProject.model.*;
import com.example.finalProject.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    ModelMapper mapper = new ModelMapper();


    @Transactional

    public MedicalReportDTO createReport(long id, String name, MedicalReportDTO medicalReportDTO) {
        Appointment appointment = appointmentRepository.findById(medicalReportDTO.getAppointmentId()).orElse(null);
        assert appointment != null;
        MedicalReport medicalReport = new MedicalReport();
        mapper.map(medicalReportDTO,medicalReport);
        medicalReport.setAppointment(appointment);
        medicalReport.setPatient(appointment.getPatient());
        medicalReport.setDoctor(appointment.getDoctor());
        medicalReport.getPrescriptions().forEach(prescription -> prescription.setMedicalReport(medicalReport));
        medicalReport.setCreatedAt(LocalDateTime.now());
        MedicalReport newReport = medicalReportRepository.save(medicalReport);
        System.out.println("123456: "+newReport);
        Activity activity = new Activity();
        activity.setAction("Medical Report Created");
        activity.setStatus("Created");
        activity.setName(medicalReport.getPatient().getName());
        activity.setUser(userRepository.findByName(medicalReport.getDoctor().getName()).orElse(null));
        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);
        return mapper.map(newReport,MedicalReportDTO.class);

    }

    public Page<MedicalReportDTO> getMedicalReports(String name, int page, int size, String sortBy, String direction, String role){
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EStatus> status = Arrays.asList(EStatus.PENDING,EStatus.CONFIRMED);
        if(role == "patient")
            return medicalReportRepository.findAllByPatientName(name,pageable).map(medicalReport -> mapper.map(medicalReport,MedicalReportDTO.class));
        else
            return medicalReportRepository.findAllByDoctorName(name,pageable).map(medicalReport -> mapper.map(medicalReport,MedicalReportDTO.class));
    }
}
