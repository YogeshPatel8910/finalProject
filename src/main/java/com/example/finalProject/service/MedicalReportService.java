package com.example.finalProject.service;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.MedicalReportDTO;
import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.model.MedicalReport;
import com.example.finalProject.model.Patient;
import com.example.finalProject.repository.MedicalReportRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    ModelMapper mapper = new ModelMapper();


    public MedicalReportDTO createReport(long id, String name, MedicalReportDTO medicalReportDTO) {
        Appointment appointment = mapper.map(appointmentService.getAppointByid(id), Appointment.class);
        Patient patient = mapper.map(patientService.getByName(appointment.getPatient().getName()),Patient.class);
        Doctor doctor = mapper.map(doctorService.getByName(name),Doctor.class);
        MedicalReport medicalReport = new MedicalReport();
        mapper.map(medicalReportDTO,medicalReport);
        medicalReport.setAppointment(appointment);
        medicalReport.setPatient(patient);
        medicalReport.setDoctor(doctor);
        MedicalReport newReport = medicalReportRepository.save(medicalReport);
        return mapper.map(newReport,MedicalReportDTO.class);

    }
}
