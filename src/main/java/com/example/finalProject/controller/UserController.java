package com.example.finalProject.controller;

import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.ERole;
import com.example.finalProject.service.UserFactory;
import com.example.finalProject.service.UserService;
import com.example.finalProject.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user profile operations
 * Uses role-based routing and factory pattern to handle different user types
 */
@RestController
@RequestMapping("api/{role}")
public class UserController {

    private final UserFactory userFactory;

    // JwtTokenUtil is injected but not used in the current implementation
    // Consider removing if not needed
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Constructor for dependency injection
     *
     * @param userFactory Factory for creating appropriate user service based on role
     * @param jwtTokenUtil JWT token utility
     */
    @Autowired
    public UserController(UserFactory userFactory, JwtTokenUtil jwtTokenUtil) {
        this.userFactory = userFactory;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Retrieves the profile of the authenticated user
     *
     * @param role Role path variable used to select appropriate service
     * @param authentication Current authenticated user
     * @return User profile data
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserByName(
            @PathVariable(name = "role") String role,
            Authentication authentication) {

        // Get appropriate service for the user role
        UserService userService = userFactory.getService(
                ERole.valueOf("ROLE_" + role.toUpperCase()));

        UserDTO user = userService.getByName(authentication.getName());

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Updates the profile of the authenticated user
     *
     * @param role Role path variable used to select appropriate service
     * @param userDTO Updated user data
     * @param authentication Current authenticated user
     * @return Updated user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable(name = "role") String role,
            @RequestBody UserDTO userDTO,
            Authentication authentication) {

        // Get appropriate service for the user role
        UserService userService = userFactory.getService(
                ERole.valueOf("ROLE_" + role.toUpperCase()));

        UserDTO updatedUser = userService.updateByName(authentication.getName(), userDTO);

        if (updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Deletes the profile of the authenticated user
     *
     * @param role Role path variable used to select appropriate service
     * @param authentication Current authenticated user
     * @return No content if successful, Not found if user doesn't exist
     */
    @DeleteMapping("/profile")
    public ResponseEntity<HttpStatus> deleteUser(
            @PathVariable(name = "role") String role,
            Authentication authentication) {

        // Get appropriate service for the user role
        UserService userService = userFactory.getService(
                ERole.valueOf("ROLE_" + role.toUpperCase()));

        boolean isDeleted = userService.deleteByName(authentication.getName());

        return isDeleted
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}