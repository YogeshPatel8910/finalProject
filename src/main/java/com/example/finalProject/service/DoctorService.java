package com.example.finalProject.service;

import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.model.ERole;
import com.example.finalProject.repository.DoctorRepository;
import com.example.finalProject.repository.RoleRepository;
import com.example.finalProject.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("doctorService")
public class DoctorService implements UserService{

    ModelMapper mapper = new ModelMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorDTO registerUser(UserDTO userDTO) {
        DoctorDTO doctorDTO = (DoctorDTO) userDTO;
        Doctor doctor = mapper.map(doctorDTO,Doctor.class);
        doctor.setRole(roleRepository.findByName(ERole.ROLE_DOCTOR));
        doctor.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
        Doctor newUser = doctorRepository.save(doctor);
        return mapToDTO(newUser);
    }

    @Override
    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction){
    Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
    Pageable pageable = PageRequest.of(page, size, sort).first().next().previous();
        return doctorRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public UserDTO getByName(String name) {
        return mapToDTO(doctorRepository.findByName(name));
    }


    private DoctorDTO mapToDTO(Doctor doctor) {
        DoctorDTO doctorDTO = mapper.map(doctor,DoctorDTO.class);
        doctor.setRole(roleRepository.findByName(ERole.ROLE_DOCTOR));
        return doctorDTO;
    }
}
