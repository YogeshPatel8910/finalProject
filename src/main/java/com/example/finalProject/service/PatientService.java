package com.example.finalProject.service;

import com.example.finalProject.dto.BranchDTO;
import com.example.finalProject.dto.PatientDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.ERole;
import com.example.finalProject.model.Patient;
import com.example.finalProject.repository.PatientRepository;
import com.example.finalProject.repository.RoleRepository;
import com.example.finalProject.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        PatientDTO patientDTO = (PatientDTO) userDTO;
        Patient patient = mapper.map(patientDTO,Patient.class);
        patient.setRole(roleRepository.findByName(ERole.ROLE_PATIENT));
        patient.setPassword(passwordEncoder.encode(patientDTO.getPassword()));
        Patient newUser = patientRepository.save(patient);
        return mapToDTO(newUser);
    }

    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO patientDTO = mapper.map(patient,PatientDTO.class);
        patient.setCreatedAt(patientDTO.getCreatedAt());
        patientDTO.setRole(ERole.ROLE_PATIENT);
        return patientDTO;
    }

    @Override
    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        return patientRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public UserDTO getByName(String name) {
        return mapToDTO(patientRepository.findByName(name));
    }

    @Override
    public UserDTO updateByName(String name, UserDTO userDTO) {
        PatientDTO patientDTO = (PatientDTO) userDTO;
        Patient patient = patientRepository.findByName(name);
        if(patient!=null){
            patient.setMobileNo(patientDTO.getMobileNo());
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

}
