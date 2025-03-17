package com.example.finalProject.service;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.model.*;
import com.example.finalProject.repository.AppointmentRepository;
import com.example.finalProject.repository.UserRepository;
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
    @Autowired
            private UserRepository userRepository;
    @Autowired
            MailService mailService;

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
        appointment.setStatus(EStatus.CONFIRMED);
        Appointment newAppointment = appointmentRepository.save(appointment);
        String patientEmail = userRepository.findByName(appointment.getPatient().getName()).orElse(new User()).getEmail();
        String doctorEmail = userRepository.findByName(appointment.getDoctor().getName()).orElse(new User()).getEmail();

        // Email content for the patient
        String patientMessage = String.format(
                "Dear %s,\n\nYour appointment with Dr. %s has been successfully scheduled on %s. " +
                        "Please arrive on time and feel free to reach out if you have any questions.\n\nBest Regards,\nHealthcare Team",
                appointment.getPatient().getName(), appointment.getDoctor().getName(), appointment.getDate().toString()
        );

        // Email content for the doctor
        String doctorMessage = String.format(
                "Dear Dr. %s,\n\nYou have a new appointment scheduled with %s on %s.\n\nBest Regards,\nHealthcare Team",
                appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate().toString()
        );

        // Send email notifications
        mailService.sendSimpleMessage(patientEmail, "Appointment Confirmation", patientMessage);
        mailService.sendSimpleMessage(doctorEmail, "New Appointment Scheduled", doctorMessage);

        return mapper.map(newAppointment,AppointmentDTO.class);
    }

    public Page<AppointmentDTO> getCurrentAppointmentsForPatient(String name, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EStatus> status = Arrays.asList(EStatus.PENDING,EStatus.CONFIRMED);
        return appointmentRepository.findAllByPatientName(name,pageable).map(appointment -> mapper.map(appointment,AppointmentDTO.class));
    }
    public Page<AppointmentDTO> getCurrentAppointmentsForAdmin(String name, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EStatus> status = Arrays.asList(EStatus.PENDING,EStatus.CONFIRMED);
        return appointmentRepository.findAll(pageable).map(appointment -> mapper.map(appointment,AppointmentDTO.class));
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
                String patientEmail = userRepository.findByName(appointment.getPatient().getName()).orElse(new User()).getEmail();
                String doctorEmail = userRepository.findByName(appointment.getDoctor().getName()).orElse(new User()).getEmail();

                // Email content for the patient
                String patientMessage = String.format(
                        "Dear %s,\n\nYour appointment scheduled on %s has been canceled. " +
                                "If you have any concerns, please contact our support team.\n\nBest Regards,\nHealthcare Team",
                        appointment.getPatient().getName(), appointment.getDate().toString()
                );

                // Email content for the doctor
                String doctorMessage = String.format(
                        "Dear Dr. %s,\n\nThe appointment with your patient, %s, scheduled on %s has been canceled.\n\nBest Regards,\nHealthcare Team",
                        appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate().toString()
                );

                // Send email notifications
                mailService.sendSimpleMessage(patientEmail, "Appointment Cancellation Notice", patientMessage);
                mailService.sendSimpleMessage(doctorEmail, "Appointment Canceled", doctorMessage);

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
            if(appointment.getDoctor().getName().equals(name) || appointment.getPatient().getName().equals(name)) {
                appointment.setStatus(EStatus.CONFIRMED);
                appointment.setDate(appointmentDTO.getDate());
                appointment.setTimeSlot(appointmentDTO.getTimeSlot());
                String patientEmail = userRepository.findByName(appointment.getPatient().getName()).orElse(new User()).getEmail();
                String doctorEmail = userRepository.findByName(appointment.getDoctor().getName()).orElse(new User()).getEmail();

                String patientMessage = String.format("Dear %s,\n\nYour appointment scheduled on %s has been rescheduled to %s. " +
                                "Please check your account for updated details.\n\nBest Regards,\nHealthcare Team",
                        appointment.getPatient().getName(), appointmentDTO.getDate(), appointment.getDate());

                String doctorMessage = String.format("Dear Dr. %s,\n\nYour patient, %s, has rescheduled their appointment. " +
                                "New appointment date: %s.\n\nBest Regards,\nHealthcare Team",
                        appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate());

                mailService.sendSimpleMessage(patientEmail, "Appointment Rescheduled", patientMessage);
                mailService.sendSimpleMessage(doctorEmail, "Appointment Reschedule Notification", doctorMessage);

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
                String patientEmail = userRepository.findByName(appointment.getPatient().getName()).orElse(new User()).getEmail();
                String doctorEmail = userRepository.findByName(appointment.getDoctor().getName()).orElse(new User()).getEmail();

                String patientMessage = String.format(
                        "Dear %s,\n\nWe noticed that you missed your appointment scheduled on %s. " +
                                "If you need to reschedule, please contact us at your earliest convenience.\n\nBest Regards,\nHealthcare Team",
                        appointment.getPatient().getName(), appointment.getDate().toString()
                );

                String doctorMessage = String.format(
                        "Dear Dr. %s,\n\nYour patient, %s, did not attend their scheduled appointment on %s. " +
                                "Please follow up as necessary.\n\nBest Regards,\nHealthcare Team",
                        appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate().toString()
                );

                mailService.sendSimpleMessage(patientEmail, "Missed Appointment Notification", patientMessage);
                mailService.sendSimpleMessage(doctorEmail, "Patient No-Show Alert", doctorMessage);

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
            if(appointment.getDoctor().getName().equals(name)) {
                appointment.setStatus(EStatus.COMPLETED);
                String patientEmail = userRepository.findByName(appointment.getPatient().getName()).orElse(new User()).getEmail();
                String doctorEmail = userRepository.findByName(appointment.getDoctor().getName()).orElse(new User()).getEmail();

                String patientMessage = String.format(
                        "Dear %s,\n\nYour medical report for the appointment on %s is now available. " +
                                "You can access it by logging into your account.\n\nBest Regards,\nHealthcare Team",
                        appointment.getPatient().getName(), appointment.getDate().toString()
                );

                String doctorMessage = String.format(
                        "Dear Dr. %s,\n\nThe medical report for your patient %s (Appointment Date: %s) has been completed.\n\nBest Regards,\nHealthcare Team",
                        appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate().toString()
                );

                mailService.sendSimpleMessage(patientEmail, "Your Medical Report is Ready", patientMessage);
                mailService.sendSimpleMessage(doctorEmail, "Medical Report Completed", doctorMessage);

                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    public AppointmentDTO getAppointById(long id) {
        return mapper.map(appointmentRepository.findById(id),AppointmentDTO.class);
    }
}
