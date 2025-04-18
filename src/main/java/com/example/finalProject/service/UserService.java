package com.example.finalProject.service;

import com.example.finalProject.dto.UserDTO;

/**
 * Interface defining common operations for user services
 * Implemented by AdminService, DoctorService, and PatientService
 */
public interface UserService {
    /**
     * Registers a new user
     *
     * @param userDTO User data transfer object
     * @return The registered user as DTO
     */
    UserDTO registerUser(UserDTO userDTO);


    /**
     * Gets a user by name
     *
     * @param name User name
     * @return User as DTO
     */
    UserDTO getByName(String name);

    /**
     * Updates a user by name
     *
     * @param name User name
     * @param userDTO Updated user data
     * @return Updated user as DTO
     */
    UserDTO updateByName(String name, UserDTO userDTO);

    /**
     * Deletes a user by name
     *
     * @param name User name
     * @return True if deleted, false if not found
     */
    boolean deleteByName(String name);
}