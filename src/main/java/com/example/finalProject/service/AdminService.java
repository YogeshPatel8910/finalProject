package com.example.finalProject.service;

import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.PatientDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.model.Patient;
import com.example.finalProject.model.User;
import com.example.finalProject.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Qualifier("adminService")
public  class AdminService implements UserService{

    ModelMapper mapper = new ModelMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDTO registerUser(UserDTO userDTO) {
        User user = mapper.map(userDTO,User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User newUser = userRepository.save(user);
        return mapper.map(newUser,UserDTO.class);
    }

    @Override
    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public UserDTO getByName(String name) {
        return mapToDTO(userRepository.findByName(name));
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO;
        if(user instanceof Patient){
            userDTO = mapper.map(user,PatientDTO.class);
        } else if (user instanceof Doctor) {
            userDTO = mapper.map(user,DoctorDTO.class);
        }
        else{
            userDTO = mapper.map(user,UserDTO.class);
        }
        userDTO.setRole(user.getRole().getName());
        return userDTO;
    }
}
