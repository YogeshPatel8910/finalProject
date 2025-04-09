package com.example.finalProject.service;

import com.example.finalProject.dto.PatientDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.*;
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
import java.util.ArrayList;
import java.util.List;

@Service("patientService")
public class PatientService implements UserService{

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

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        PatientDTO patientDTO = (PatientDTO) userDTO;
        Patient patient = mapper.map(patientDTO,Patient.class);
        patient.setRole(roleRepository.findByName(ERole.ROLE_PATIENT));
        patient.setPassword(passwordEncoder.encode(patientDTO.getPassword()));
        patient.setCreatedAt(LocalDateTime.now());
        Patient newUser = patientRepository.save(patient);
        Activity activity = new Activity();
        activity.setAction("New Patient Registration");
        activity.setStatus("Complete");
        activity.setName(newUser.getName());
        activity.setUser(userRepository.findByName("admin").orElse(null));
        activity.setTime(LocalDateTime.now());
        activityRepsoitory.save(activity);
        return mapper.map(newUser,PatientDTO.class);
    }

    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        return patientRepository.findAll(pageable).map(patient -> mapper.map(patient,PatientDTO.class));
    }

    @Override
    public UserDTO getByName(String name) {
        return mapper.map(patientRepository.findByName(name),PatientDTO.class);
    }


    @Override
    @Transactional
    public UserDTO updateByName(String name, UserDTO userDTO) {
        PatientDTO patientDTO = (PatientDTO) userDTO;
        Patient patient = patientRepository.findByName(name);
        if(patient!=null){
//            mapper.map(patientDTO,patient);
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.map(patientDTO, patient);
            System.out.println(patient);
            patient.setUpdatedAt(LocalDateTime.now());
            return mapper.map(patient, PatientDTO.class);
        }
        else
            return null;
    }

    @Override
    public boolean deleteByName(String name) {
        boolean isPresent = patientRepository.existsByName(name);
        if(isPresent){
            patientRepository.deleteByName(name);
            return true;
        }
        else
            return false;
    }

    public List<Branch> getData() {
        return branchService.getAll();
    }
}
