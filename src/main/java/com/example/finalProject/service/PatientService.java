package com.example.finalProject.service;

import com.example.finalProject.dto.PatientDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.Activity;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.ERole;
import com.example.finalProject.model.Patient;
import com.example.finalProject.repository.ActivityRepsoitory;
import com.example.finalProject.repository.PatientRepository;
import com.example.finalProject.repository.RoleRepository;
import com.example.finalProject.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for patient operations
 * Implements UserService for patient-specific functionality
 */
@Service("patientService")
public class PatientService implements UserService {

    ModelMapper mapper = new ModelMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ActivityRepsoitory activityRepsoitory;

    /**
     * Registers a new patient
     *
     * @param userDTO Patient information as UserDTO
     * @return The registered patient as UserDTO
     */
    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        PatientDTO patientDTO = (PatientDTO) userDTO;
        Patient patient = mapper.map(patientDTO, Patient.class);

        // Set patient role and encrypt password
        patient.setRole(roleRepository.findByName(ERole.ROLE_PATIENT));
        patient.setPassword(passwordEncoder.encode(patientDTO.getPassword()));
        patient.setCreatedAt(LocalDateTime.now());

        // Save patient
        Patient newUser = patientRepository.save(patient);

        // Create activity log
        Activity activity = new Activity();
        activity.setAction("New Patient Registration");
        activity.setStatus("Complete");
        activity.setName(newUser.getName());
        activity.setUser(userRepository.findByName("admin").orElse(null));
        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);

        return mapper.map(newUser, PatientDTO.class);
    }

    /**
     * Retrieves all patients with pagination and sorting
     *
     * @param page Page number
     * @param size Page size
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @return Page of patient DTOs
     */
    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return patientRepository.findAll(pageable).map(patient -> mapper.map(patient, PatientDTO.class));
    }

    /**
     * Gets a patient by name
     *
     * @param name Patient name
     * @return Patient as UserDTO
     */
    @Override
    public UserDTO getByName(String name) {
        return mapper.map(patientRepository.findByName(name), PatientDTO.class);
    }

    /**
     * Updates a patient by name
     *
     * @param name Patient name
     * @param userDTO Updated patient information
     * @return Updated patient as UserDTO
     */
    @Override
    @Transactional
    public UserDTO updateByName(String name, UserDTO userDTO) {
        PatientDTO patientDTO = (PatientDTO) userDTO;
        Patient patient = patientRepository.findByName(name);
        if (patient != null) {
            // Configure mapper to only map non-null properties
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.map(patientDTO, patient);
            System.out.println(patient);
            patient.setUpdatedAt(LocalDateTime.now());
            return mapper.map(patient, PatientDTO.class);
        } else {
            return null;
        }
    }

    /**
     * Deletes a patient by name
     *
     * @param name Patient name
     * @return True if deleted, false if not found
     */
    @Override
    public boolean deleteByName(String name) {
        boolean isPresent = patientRepository.existsByName(name);
        if (isPresent) {
            patientRepository.deleteByName(name);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets all branches for display to patients
     *
     * @return List of branches
     */
    public List<Branch> getData() {
        return branchService.getAll();
    }
}