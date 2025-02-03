package com.example.finalProject.controller;

import com.example.finalProject.dto.LoginDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.service.AuthService;
import com.example.finalProject.service.UserFactory;
import com.example.finalProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserService userService = userFactory.getService(userDTO.getRole());
        UserDTO response = userService.registerUser(userDTO);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getName(), loginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);
        String token = authService.generateToken(userDetails.getUsername(),role);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("redirectUrl", "http://localhost:8081/api/" + role.toLowerCase().substring(5) + "/" + userDetails.getUsername());
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token.substring(7));
        return ResponseEntity.ok(isValid);
    }
}
