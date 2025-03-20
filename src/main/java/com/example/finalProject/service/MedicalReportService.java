package com.example.finalProject.service;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.MedicalReportDTO;
import com.example.finalProject.dto.PrescriptionDTO;
import com.example.finalProject.model.*;
import com.example.finalProject.repository.AppointmentRepository;
import com.example.finalProject.repository.MedicalReportRepository;
import com.example.finalProject.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        MedicalReport newReport = medicalReportRepository.save(medicalReport);
        System.out.println("123456: "+newReport);
        return mapper.map(newReport,MedicalReportDTO.class);

    }
}
