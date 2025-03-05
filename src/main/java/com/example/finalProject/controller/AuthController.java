package com.example.finalProject.controller;

import com.example.finalProject.dto.LoginDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.service.AuthService;
import com.example.finalProject.service.UserFactory;
import com.example.finalProject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService  authService;

    @Autowired
    private UserFactory userFactory;



    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> registerUser(@RequestBody UserDTO userDTO) {
        UserService userService = userFactory.getService(userDTO.getRoleName());
        Map<String, Object> response = new HashMap<>();
        UserDTO newUser = userService.registerUser(userDTO);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDTO.getName(),userDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<String> role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst();
        if (role.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "User has no assigned role"));
        }
        String token = authService.generateToken(userDetails.getUsername(),role.get());
        response.put("token", token);
        response.put("role", role.get().toLowerCase().substring(5) );
        response.put("user", newUser);
        System.out.println(response);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginDTO loginDTO) {
        try{
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getName(), loginDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<String> role = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst();
            if (role.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "User has no assigned role"));
            }
            String token = authService.generateToken(userDetails.getUsername(),role.get());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role.get().toLowerCase().substring(5) );
            return ResponseEntity.ok(response);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token.substring(7));
        return ResponseEntity.ok(isValid);
    }
}
