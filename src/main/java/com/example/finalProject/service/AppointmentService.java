package com.example.finalProject.service;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.model.*;
import com.example.finalProject.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private DepartmentService departmentService;

    ModelMapper mapper = new ModelMapper();

    public AppointmentDTO createAppointment(String user, AppointmentDTO appointmentDTO) {
        Patient patient = mapper.map(patientService.getByName(user),Patient.class);
        Doctor doctor = mapper.map(doctorService.getByName(appointmentDTO.getDoctorName()),Doctor.class);
        Branch branch = mapper.map(branchService.getByName(appointmentDTO.getBranchName()),Branch.class);
        Department department = mapper.map(departmentService.getByName(appointmentDTO.getDepartmentName()),Department.class);
        Appointment appointment = mapper.map(appointmentDTO,Appointment.class);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setBranch(branch);
        appointment.setDepartment(department);
        appointment.setStatus(EStatus.PENDING);
        Appointment newAppointment = appointmentRepository.save(appointment);
        return mapper.map(newAppointment,AppointmentDTO.class);
    }

    public Page<AppointmentDTO> getCurrentAppointmentsForPatient(String name, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EStatus> status = Arrays.asList(EStatus.PENDING,EStatus.CONFIRMED);
        return appointmentRepository.findAllByPatientNameAndStatusIn(name,status,pageable).map(appointment -> mapper.map(appointment,AppointmentDTO.class));
    }

    public Page<AppointmentDTO> getAppointmentHistoryForPatient(String name, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EStatus> status = Arrays.asList(EStatus.NO_SHOW,EStatus.COMPLETED,EStatus.CANCELLED);
        return appointmentRepository.findAllByPatientNameAndStatusIn(name,status,pageable).map(appointment -> mapper.map(appointment,AppointmentDTO.class));
    }

    public Page<AppointmentDTO> getCurrentAppointmentsForDoctor(String name, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
//        List<EStatus> status = Arrays.asList(EStatus.PENDING,EStatus.CONFIRMED);
//        return appointmentRepository.findAllByDoctorNameAndStatusIn(name,status,pageable).map(appointment -> mapper.map(appointment,AppointmentDTO.class));
        return appointmentRepository.findAllByDoctorName(name,pageable).map(appointment-> mapper.map(appointment,AppointmentDTO.class));
    }

    public Page<AppointmentDTO> getAppointmentHistoryForDoctor(String name, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EStatus> status = Arrays.asList(EStatus.NO_SHOW,EStatus.COMPLETED,EStatus.CANCELLED);
        return appointmentRepository.findAllByDoctorNameAndStatusIn(name,status,pageable).map(appointment -> mapper.map(appointment,AppointmentDTO.class));
    }

    @Transactional
    public boolean cancelAppointment(String name, long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if(appointment!=null){
            if(appointment.getPatient().getName().equals(name)) {
                appointment.setStatus(EStatus.CANCELLED);
                return true;
            }
            else
                return false;
        }
        else
            return false;

    }

    @Transactional
    public boolean rescheduleAppointment(String name, long id, AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if(appointment!=null){
            if(appointment.getPatient().getName().equals(name)) {
                appointment.setStatus(EStatus.PENDING);
                appointment.setDate(appointmentDTO.getDate());
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    @Transactional
    public boolean confirmAppointment(String name, long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if(appointment!=null){
            if(appointment.getPatient().getName().equals(name)) {
                appointment.setStatus(EStatus.CONFIRMED);
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    @Transactional
    public boolean noShowAppointment(String name, long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if(appointment!=null){
            if(appointment.getPatient().getName().equals(name)) {
                appointment.setStatus(EStatus.NO_SHOW);
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    @Transactional
    public boolean completeAppointment(String name, long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if(appointment!=null){
            if(appointment.getPatient().getName().equals(name)) {
                appointment.setStatus(EStatus.COMPLETED);
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    public AppointmentDTO getAppointByid(long id) {
        return mapper.map(appointmentRepository.findById(id),AppointmentDTO.class);
    }
}
