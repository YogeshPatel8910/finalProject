package com.example.finalProject.service;

import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.Activity;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.model.ERole;
import com.example.finalProject.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Service class for managing Doctor entities
 * Implements UserService interface and provides doctor-specific operations
 */
@Service("doctorService")
public class DoctorService implements UserService {

    // ModelMapper for entity to DTO conversion and vice versa
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ActivityRepsoitory activityRepsoitory;

    /**
     * Registers a new doctor user
     * Admin role required for this operation
     *
     * @param userDTO User data transfer object (should be DoctorDTO)
     * @return Registered doctor as DTO
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorDTO registerUser(UserDTO userDTO) {
        DoctorDTO doctorDTO = (DoctorDTO) userDTO;
        Doctor doctor = mapper.map(doctorDTO, Doctor.class);

        // Set role, encode password, and assign branch and department
        doctor.setRole(roleRepository.findByName(ERole.ROLE_DOCTOR));
        doctor.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
        doctor.setBranch(branchRepository.findByName(doctorDTO.getBranchName()));
        doctor.setDepartment(departmentRepository.findByName(doctorDTO.getDepartmentName()));
        doctor.setCreatedAt(LocalDateTime.now());

        Doctor newUser = doctorRepository.save(doctor);

        // Log the activity
        Activity activity = new Activity();
        activity.setAction("New Doctor Registration");
        activity.setStatus("Complete");
        activity.setName(newUser.getName());
        activity.setUser(userRepository.findByName("admin").orElse(null));
        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);

        return mapper.map(newUser, DoctorDTO.class);
    }

    /**
     * Retrieves all doctors with pagination and sorting
     *
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @return Page of DoctorDTO objects
     */
    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return doctorRepository.findAll(pageable).map(doctor -> mapper.map(doctor, DoctorDTO.class));
    }

    /**
     * Finds doctor by name
     *
     * @param name Doctor name
     * @return Doctor DTO if found
     */
    @Override
    public UserDTO getByName(String name) {
        return mapper.map(doctorRepository.findByName(name), DoctorDTO.class);
    }

    /**
     * Updates an existing doctor
     *
     * @param name Doctor name to update
     * @param userDTO Updated doctor data
     * @return Updated doctor as DTO, or null if not found
     */
    @Override
    @Transactional
    public UserDTO updateByName(String name, UserDTO userDTO) {
        DoctorDTO doctorDTO = (DoctorDTO) userDTO;
        Doctor doctor = doctorRepository.findByName(name);
        if(doctor != null) {
            doctor.setMobileNo(doctorDTO.getMobileNo());
            doctor.setUpdatedAt(LocalDateTime.now());
            return mapper.map(doctor, DoctorDTO.class);
        } else {
            return null;
        }
    }

    /**
     * Deletes a doctor by name
     *
     * @param name Doctor name to delete
     * @return true if deleted, false if not found
     */
    @Override
    public boolean deleteByName(String name) {
        boolean isPresent = doctorRepository.existsByName(name);
        if(isPresent) {
            doctorRepository.deleteByName(name);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets available dates for a doctor
     *
     * @param user Doctor username
     * @param dates Set of dates to add to availability
     * @return Updated set of available dates
     */
    @Transactional
    public Set<LocalDate> setDate(String user, Set<LocalDate> dates) {
        Doctor doctor = doctorRepository.findByName(user);
        if(doctor.getAvailableDays() == null) {
            doctor.setAvailableDays(dates);
        } else {
            doctor.getAvailableDays().addAll(dates);
        }
        doctorRepository.save(doctor);
        return doctor.getAvailableDays();
    }

    /**
     * Removes dates from a doctor's availability
     *
     * @param user Doctor username
     * @param dates Set of dates to remove from availability
     * @return Updated set of available dates
     */
    @Transactional
    public Set<LocalDate> deleteDate(String user, Set<LocalDate> dates) {
        Doctor doctor = doctorRepository.findByName(user);
        doctor.getAvailableDays().removeAll(dates);
        doctorRepository.save(doctor);
        return doctor.getAvailableDays();
    }

    /**
     * Gets all available dates for a doctor
     *
     * @param user Doctor username
     * @return Set of available dates
     */
    public Set<LocalDate> getDate(String user) {
        Doctor doctor = doctorRepository.findByName(user);
        if(doctor.getAvailableDays() == null) {
            return Collections.emptySet();
        }
        return doctor.getAvailableDays();
    }

    /**
     * Gets doctor entity by name
     *
     * @param doctor Doctor name
     * @return Doctor entity
     */
    public Doctor getDoctor(String doctor) {
        return doctorRepository.findByName(doctor);
    }

    /**
     * Gets all doctors (without pagination)
     *
     * @return List of all doctors as DTOs
     */
    public List<DoctorDTO> getAllDoctor() {
        return doctorRepository.findAll().stream()
                .map(doctor -> mapper.map(doctor, DoctorDTO.class))
                .toList();
    }
}