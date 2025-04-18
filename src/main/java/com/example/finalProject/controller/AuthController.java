package com.example.finalProject.controller;

import com.example.finalProject.dto.LoginDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.service.AuthService;
import com.example.finalProject.service.MailService;
import com.example.finalProject.service.UserFactory;
import com.example.finalProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for authentication operations.
 * Handles user registration, login, and token validation.
 * All endpoints in this controller are publicly accessible.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private MailService mailService;

    /**
     * Registers a new user based on their role.
     * Uses a factory pattern to determine the appropriate service for user registration.
     * Sends a welcome email to the newly registered user.
     *
     * @param userDTO User details for registration
     * @return Response containing the newly created user
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserDTO userDTO) {
        UserService userService = userFactory.getService(userDTO.getRoleName());
        Map<String, Object> response = new HashMap<>();
        UserDTO newUser = userService.registerUser(userDTO);
        response.put("user", newUser);

        // Send welcome email to the registered user
        String subject = "Welcome to Our Platform!";
        String body = "Dear " + newUser.getName() + ",\n\nYour account has been successfully registered.\n\nBest Regards,\nTeam";
        mailService.sendSimpleMessage(newUser.getEmail(), subject, body);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and generates a JWT token.
     * Extracts the user's role and includes it in the response.
     *
     * @param loginDTO Login credentials (username and password)
     * @return Response containing JWT token and user role
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        try {
            // Create authentication token and authenticate
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginDTO.getName(), loginDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Extract user details and role
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<String> role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst();

            if (role.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "User has no assigned role"));
            }

            // Generate JWT token and prepare response
            String token = authService.generateToken(userDetails.getUsername(), role.get());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            // Remove 'ROLE_' prefix from role name
            response.put("role", role.get().toLowerCase().substring(5));

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    /**
     * Validates a JWT token.
     * Removes the 'Bearer ' prefix before validation.
     *
     * @param token JWT token with 'Bearer ' prefix
     * @return Boolean indicating if the token is valid
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        // Remove 'Bearer ' prefix from token
        boolean isValid = authService.validateToken(token.substring(7));
        return ResponseEntity.ok(isValid);
    }
}