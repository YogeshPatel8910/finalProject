package com.example.finalProject.service;

import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.PatientDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.model.ERole;
import com.example.finalProject.model.Patient;
import com.example.finalProject.model.User;
import com.example.finalProject.repository.UserRepository;
import jakarta.transaction.Transactional;
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

import java.time.LocalDateTime;

@Service("adminService")
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
        user.setCreatedAt(LocalDateTime.now());
        User newUser = userRepository.save(user);
        return mapper.map(newUser,UserDTO.class);
    }

    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction, ERole search) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        if(search==null)
            return userRepository.findAll(pageable).map(this::mapToDTO);
        else
            return userRepository.findAllByRoleName(search,pageable).map(this::mapToDTO);
    }

    @Override
    public UserDTO getByName(String name) {
        return mapper.map(userRepository.findByName(name), UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO updateByName(String name, UserDTO userDTO) {
        User user = userRepository.findByName(name).orElse(null);
        if(user!=null){
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setMobileNo(userDTO.getMobileNo());
            user.setUpdatedAt(LocalDateTime.now());
            return mapper.map(user, UserDTO.class);
        }
        else
            return null;
    }

    @Override
    public boolean deleteByName(String name) {
        boolean isPresent = userRepository.existsByName(name);
        if(isPresent){
            userRepository.deleteByName(name);
            return true;
        }
        else
            return false;
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
        return userDTO;
    }


    public boolean deleteById(long id) {
        boolean isPresent = userRepository.existsById(id);
        if(isPresent){
            userRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }

    public UserDTO getById(long id) {
        return mapToDTO(userRepository.findById(id).orElse(null));
    }
}
