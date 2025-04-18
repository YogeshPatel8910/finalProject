package com.example.finalProject.service;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.model.*;
import com.example.finalProject.repository.ActivityRepsoitory;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Service responsible for handling appointment operations including creation,
 * retrieval, cancellation, and status updates. Also handles email notifications
 * for appointment events.
 */
@Service
public class AppointmentService {

    private final ModelMapper mapper = new ModelMapper();

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
    private MailService mailService;

    @Autowired
    private ActivityRepsoitory activityRepsoitory;

    /**
     * Creates a new appointment with the specified details and sends notification emails.
     *
     * @param user Username of the patient making the appointment
     * @param appointmentDTO DTO containing appointment details
     * @return DTO of the created appointment
     */
    public AppointmentDTO createAppointment(String user, AppointmentDTO appointmentDTO) {
        // Map entities from their respective services
        Patient patient = mapper.map(patientService.getByName(user), Patient.class);
        Doctor doctor = mapper.map(doctorService.getByName(appointmentDTO.getDoctorName()), Doctor.class);
        Branch branch = mapper.map(branchService.getByName(appointmentDTO.getBranchName()), Branch.class);
        Department department = mapper.map(departmentService.getByName(appointmentDTO.getDepartmentName()), Department.class);

        // Create and configure appointment
        Appointment appointment = mapper.map(appointmentDTO, Appointment.class);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setBranch(branch);
        appointment.setDepartment(department);
        appointment.setStatus(EStatus.CONFIRMED);

        // Save appointment
        Appointment newAppointment = appointmentRepository.save(appointment);

        // Get email addresses
        String patientEmail = userRepository.findByName(appointment.getPatient().getName())
                .orElse(new User()).getEmail();
        String doctorEmail = userRepository.findByName(appointment.getDoctor().getName())
                .orElse(new User()).getEmail();

        // Email notifications
        String patientMessage = String.format(
                "Dear %s,\n\nYour appointment with Dr. %s has been successfully scheduled on %s. " +
                        "Please arrive on time and feel free to reach out if you have any questions.\n\nBest Regards,\nHealthcare Team",
                appointment.getPatient().getName(), appointment.getDoctor().getName(), appointment.getDate().toString()
        );

        String doctorMessage = String.format(
                "Dear Dr. %s,\n\nYou have a new appointment scheduled with %s on %s.\n\nBest Regards,\nHealthcare Team",
                appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate().toString()
        );

        // Send emails
        mailService.sendSimpleMessage(patientEmail, "Appointment Confirmation", patientMessage);
        mailService.sendSimpleMessage(doctorEmail, "New Appointment Scheduled", doctorMessage);

        // Log activity
        Activity activity = new Activity();
        activity.setAction("Appointment Confirmed");
        activity.setStatus("Scheduled");
        activity.setName(doctor.getName());
        activity.setUser(userRepository.findByName(patient.getName()).orElse(null));
        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);

        return mapper.map(newAppointment, AppointmentDTO.class);
    }

    /**
     * Retrieves current appointments for a patient.
     *
     * @param name Patient username
     * @param page Page number
     * @param size Items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction
     * @return Page of appointment DTOs
     */
    public Page<AppointmentDTO> getCurrentAppointmentsForPatient(String name, int page, int size,
                                                                 String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return appointmentRepository.findAllByPatientName(name, pageable)
                .map(appointment -> mapper.map(appointment, AppointmentDTO.class));
    }

    /**
     * Retrieves all appointments for admin view.
     *
     * @param name Admin username
     * @param page Page number
     * @param size Items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction
     * @return Page of appointment DTOs
     */
    public Page<AppointmentDTO> getCurrentAppointmentsForAdmin(String name, int page, int size,
                                                               String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return appointmentRepository.findAll(pageable)
                .map(appointment -> mapper.map(appointment, AppointmentDTO.class));
    }

    /**
     * Retrieves appointment history for a patient (completed, canceled, no-show).
     *
     * @param name Patient username
     * @param page Page number
     * @param size Items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction
     * @return Page of appointment DTOs
     */
    public Page<AppointmentDTO> getAppointmentHistoryForPatient(String name, int page, int size,
                                                                String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        List<EStatus> status = Arrays.asList(EStatus.NO_SHOW, EStatus.COMPLETED, EStatus.CANCELLED);
        return appointmentRepository.findAllByPatientNameAndStatusIn(name, status, pageable)
                .map(appointment -> mapper.map(appointment, AppointmentDTO.class));
    }

    /**
     * Retrieves current appointments for a doctor.
     *
     * @param name Doctor username
     * @param page Page number
     * @param size Items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction
     * @return Page of appointment DTOs
     */
    public Page<AppointmentDTO> getCurrentAppointmentsForDoctor(String name, int page, int size,
                                                                String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return appointmentRepository.findAllByDoctorName(name, pageable)
                .map(appointment -> mapper.map(appointment, AppointmentDTO.class));
    }

    /**
     * Retrieves appointment history for a doctor (completed, canceled, no-show).
     *
     * @param name Doctor username
     * @param page Page number
     * @param size Items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction
     * @return Page of appointment DTOs
     */
    public Page<AppointmentDTO> getAppointmentHistoryForDoctor(String name, int page, int size,
                                                               String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        List<EStatus> status = Arrays.asList(EStatus.NO_SHOW, EStatus.COMPLETED, EStatus.CANCELLED);
        return appointmentRepository.findAllByDoctorNameAndStatusIn(name, status, pageable)
                .map(appointment -> mapper.map(appointment, AppointmentDTO.class));
    }

    /**
     * Helper method to create Pageable object for pagination and sorting.
     */
    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        return PageRequest.of(page, size, sort);
    }

    /**
     * Cancels an appointment and sends email notifications.
     *
     * @param name Username of the user canceling the appointment
     * @param id Appointment ID
     * @return true if canceled successfully, false otherwise
     */
    @Transactional
    public boolean cancelAppointment(String name, long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return false;
        }

        if (!appointment.getPatient().getName().equals(name)) {
            return false;
        }

        // Update status
        appointment.setStatus(EStatus.CANCELLED);

        // Get email addresses
        String patientEmail = userRepository.findByName(appointment.getPatient().getName())
                .orElse(new User()).getEmail();
        String doctorEmail = userRepository.findByName(appointment.getDoctor().getName())
                .orElse(new User()).getEmail();

        // Email notifications
        String patientMessage = String.format(
                "Dear %s,\n\nYour appointment scheduled on %s has been canceled. " +
                        "If you have any concerns, please contact our support team.\n\nBest Regards,\nHealthcare Team",
                appointment.getPatient().getName(), appointment.getDate().toString()
        );

        String doctorMessage = String.format(
                "Dear Dr. %s,\n\nThe appointment with your patient, %s, scheduled on %s has been canceled.\n\nBest Regards,\nHealthcare Team",
                appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate().toString()
        );

        // Send emails
        mailService.sendSimpleMessage(patientEmail, "Appointment Cancellation Notice", patientMessage);
        mailService.sendSimpleMessage(doctorEmail, "Appointment Canceled", doctorMessage);

        // Log activity
        Activity activity = new Activity();
        activity.setAction("Appointment Canceled");
        activity.setStatus("Canceled");
        activity.setName(appointment.getDoctor().getName());
        activity.setUser(userRepository.findByName(appointment.getPatient().getName()).orElse(null));
        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);

        return true;
    }

    /**
     * Reschedules an appointment and sends email notifications.
     *
     * @param name Username of the user rescheduling the appointment
     * @param id Appointment ID
     * @param appointmentDTO DTO with new appointment details
     * @return true if rescheduled successfully, false otherwise
     */
    @Transactional
    public boolean rescheduleAppointment(String name, long id, AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return false;
        }

        if (!appointment.getDoctor().getName().equals(name) &&
                !appointment.getPatient().getName().equals(name)) {
            return false;
        }

        // Update appointment
        appointment.setStatus(EStatus.CONFIRMED);
        appointment.setDate(appointmentDTO.getDate());
        appointment.setTimeSlot(appointmentDTO.getTimeSlot());

        // Get email addresses
        String patientEmail = userRepository.findByName(appointment.getPatient().getName())
                .orElse(new User()).getEmail();
        String doctorEmail = userRepository.findByName(appointment.getDoctor().getName())
                .orElse(new User()).getEmail();

        // Email notifications
        String patientMessage = String.format(
                "Dear %s,\n\nYour appointment scheduled on %s has been rescheduled to %s. " +
                        "Please check your account for updated details.\n\nBest Regards,\nHealthcare Team",
                appointment.getPatient().getName(), appointmentDTO.getDate(), appointment.getDate()
        );

        String doctorMessage = String.format(
                "Dear Dr. %s,\n\nYour patient, %s, has rescheduled their appointment. " +
                        "New appointment date: %s.\n\nBest Regards,\nHealthcare Team",
                appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate()
        );

        // Send emails
        mailService.sendSimpleMessage(patientEmail, "Appointment Rescheduled", patientMessage);
        mailService.sendSimpleMessage(doctorEmail, "Appointment Reschedule Notification", doctorMessage);

        // Log activity
        Activity activity = new Activity();
        activity.setAction("Appointment Rescheduled");
        activity.setStatus("Rescheduled");
        activity.setName(appointment.getDoctor().getName());

        if (appointment.getDoctor().getName().equals(name)) {
            activity.setUser(userRepository.findByName(appointment.getDoctor().getName()).orElse(null));
        } else {
            activity.setUser(userRepository.findByName(appointment.getPatient().getName()).orElse(null));
        }

        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);

        return true;
    }

    /**
     * Marks an appointment as no-show and sends email notifications.
     *
     * @param name Username of the user marking the appointment
     * @param id Appointment ID
     * @return true if marked successfully, false otherwise
     */
    @Transactional
    public boolean noShowAppointment(String name, long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null || !appointment.getPatient().getName().equals(name)) {
            return false;
        }

        // Update status
        appointment.setStatus(EStatus.NO_SHOW);

        // Get email addresses
        String patientEmail = userRepository.findByName(appointment.getPatient().getName())
                .orElse(new User()).getEmail();
        String doctorEmail = userRepository.findByName(appointment.getDoctor().getName())
                .orElse(new User()).getEmail();

        // Email notifications
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

        // Send emails
        mailService.sendSimpleMessage(patientEmail, "Missed Appointment Notification", patientMessage);
        mailService.sendSimpleMessage(doctorEmail, "Patient No-Show Alert", doctorMessage);

        return true;
    }

    /**
     * Marks an appointment as completed and sends email notifications.
     *
     * @param name Username of the doctor completing the appointment
     * @param id Appointment ID
     * @return true if completed successfully, false otherwise
     */
    @Transactional
    public boolean completeAppointment(String name, long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null || !appointment.getDoctor().getName().equals(name)) {
            return false;
        }

        // Update status
        appointment.setStatus(EStatus.COMPLETED);

        // Get email addresses
        String patientEmail = userRepository.findByName(appointment.getPatient().getName())
                .orElse(new User()).getEmail();
        String doctorEmail = userRepository.findByName(appointment.getDoctor().getName())
                .orElse(new User()).getEmail();

        // Email notifications
        String patientMessage = String.format(
                "Dear %s,\n\nYour medical report for the appointment on %s is now available. " +
                        "You can access it by logging into your account.\n\nBest Regards,\nHealthcare Team",
                appointment.getPatient().getName(), appointment.getDate().toString()
        );

        String doctorMessage = String.format(
                "Dear Dr. %s,\n\nThe medical report for your patient %s (Appointment Date: %s) has been completed.\n\nBest Regards,\nHealthcare Team",
                appointment.getDoctor().getName(), appointment.getPatient().getName(), appointment.getDate().toString()
        );

        // Send emails
        mailService.sendSimpleMessage(patientEmail, "Your Medical Report is Ready", patientMessage);
        mailService.sendSimpleMessage(doctorEmail, "Medical Report Completed", doctorMessage);

        return true;
    }

    /**
     * Retrieves an appointment by its ID.
     *
     * @param id Appointment ID
     * @return DTO of the appointment
     */
    public AppointmentDTO getAppointById(long id) {
        return mapper.map(appointmentRepository.findById(id), AppointmentDTO.class);
    }
}