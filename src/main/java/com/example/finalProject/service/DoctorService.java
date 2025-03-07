package com.example.finalProject.service;

import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.model.ERole;
import com.example.finalProject.repository.DoctorRepository;
import com.example.finalProject.repository.RoleRepository;
import com.example.finalProject.repository.UserRepository;
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
import java.util.List;
import java.util.Set;

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
        doctor.setCreatedAt(LocalDateTime.now());
        Doctor newUser = doctorRepository.save(doctor);
        return mapper.map(newUser,DoctorDTO.class);
    }

    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction){
    Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
    Pageable pageable = PageRequest.of(page, size, sort);
        return doctorRepository.findAll(pageable).map(doctor -> mapper.map(doctor,DoctorDTO.class));
    }

    @Override
    public UserDTO getByName(String name) {
        return mapper.map(doctorRepository.findByName(name),DoctorDTO.class);
    }

    @Override
    @Transactional
    public UserDTO updateByName(String name, UserDTO userDTO) {
        DoctorDTO doctorDTO = (DoctorDTO) userDTO;
        Doctor doctor = doctorRepository.findByName(name);
        if(doctor!=null){
            doctor.setMobileNo(doctorDTO.getMobileNo());
            doctor.setUpdatedAt(LocalDateTime.now());
            return mapper.map(doctor, DoctorDTO.class);
        }
        else
            return null;
    }

    @Override
    public boolean deleteByName(String name) {
        boolean isPresent = doctorRepository.existsByName(name);
        if(isPresent){
            doctorRepository.deleteByName(name);
            return true;
        }
        else
            return false;
    }

    @Transactional
    public Set<LocalDate> setDate(String user,Set<LocalDate> dates) {
        Doctor doctor = doctorRepository.findByName(user);
        doctor.getAvailableDays().addAll(dates);
        doctorRepository.save(doctor);
        return doctor.getAvailableDays();
    }
    @Transactional
    public Set<LocalDate> deleteDate(String user,Set<LocalDate> dates) {
        Doctor doctor = doctorRepository.findByName(user);
        doctor.getAvailableDays().removeAll(dates);
        doctorRepository.save(doctor);
        return doctor.getAvailableDays();
    }
    public Set<LocalDate> getDate(String user) {
        Doctor doctor = doctorRepository.findByName(user);
        return doctor.getAvailableDays();
    }

    public Doctor getDoctor(String doctor) {
        return doctorRepository.findByName(doctor);
    }

    public List<DoctorDTO> getAllDoctor() {
        return doctorRepository.findAll().stream().map(doctor -> mapper.map(doctor,DoctorDTO.class)).toList();
    }
}
