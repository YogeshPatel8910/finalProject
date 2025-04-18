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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class responsible for administrator operations related to user management.
 * Handles user registration, retrieval, updating, and deletion with admin privileges.
 */
@Service("adminService")
public class AdminService implements UserService {

    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with admin privileges.
     * Encrypts the password and sets creation timestamp.
     *
     * @param userDTO Data transfer object containing user information
     * @return DTO of the created user
     */
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDTO registerUser(UserDTO userDTO) {
        User user = mapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        User newUser = userRepository.save(user);
        return mapper.map(newUser, UserDTO.class);
    }

    /**
     * Retrieves paginated list of users with sorting and optional role filtering.
     *
     * @param page Current page number
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @param search Role filter (optional)
     * @return Page of UserDTO objects
     */
    public Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction, ERole search) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search == null) {
            return userRepository.findAll(pageable).map(this::mapToDTO);
        } else {
            return userRepository.findAllByRoleName(search, pageable).map(this::mapToDTO);
        }
    }

    /**
     * Retrieves a user by username.
     *
     * @param name Username to search for
     * @return DTO of the found user
     */
    @Override
    public UserDTO getByName(String name) {
        return mapper.map(userRepository.findByName(name), UserDTO.class);
    }

    /**
     * Updates user information by username.
     *
     * @param name Username to update
     * @param userDTO Updated user information
     * @return DTO of the updated user or null if user not found
     */
    @Override
    @Transactional
    public UserDTO updateByName(String name, UserDTO userDTO) {
        User user = userRepository.findByName(name).orElse(null);
        if (user != null) {
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setMobileNo(userDTO.getMobileNo());
            user.setUpdatedAt(LocalDateTime.now());
            return mapper.map(user, UserDTO.class);
        } else {
            return null;
        }
    }

    /**
     * Deletes a user by username.
     *
     * @param name Username to delete
     * @return true if user was deleted, false if not found
     */
    @Override
    public boolean deleteByName(String name) {
        boolean isPresent = userRepository.existsByName(name);
        if (isPresent) {
            userRepository.deleteByName(name);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Maps User entity to appropriate DTO subclass based on user type.
     *
     * @param user User entity to map
     * @return Appropriate UserDTO subclass
     */
    private UserDTO mapToDTO(User user) {
        if (user instanceof Patient) {
            return mapper.map(user, PatientDTO.class);
        } else if (user instanceof Doctor) {
            return mapper.map(user, DoctorDTO.class);
        } else {
            return mapper.map(user, UserDTO.class);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id User ID to delete
     * @return true if user was deleted, false if not found
     */
    public boolean deleteById(long id) {
        boolean isPresent = userRepository.existsById(id);
        if (isPresent) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id User ID to retrieve
     * @return DTO of the found user
     */
    public UserDTO getById(long id) {
        return mapToDTO(userRepository.findById(id).orElse(null));
    }
}