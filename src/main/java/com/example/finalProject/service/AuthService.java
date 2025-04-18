package com.example.finalProject.service;

import com.example.finalProject.model.User;
import com.example.finalProject.repository.UserRepository;
import com.example.finalProject.util.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Authentication service responsible for user authentication operations.
 * Handles user loading for Spring Security and JWT token operations.
 */
@Service
@Qualifier("authService")
public class AuthService implements UserDetailsService {

    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Generates a JWT token for authenticated users.
     *
     * @param username The username for which to generate the token
     * @param roles The roles assigned to the user
     * @return Generated JWT token string
     */
    public String generateToken(String username, String roles) {
        return jwtTokenUtil.generateToken(username, roles);
    }

    /**
     * Validates a JWT token's authenticity and checks if the user exists.
     *
     * @param token JWT token to validate
     * @return True if token is valid and user exists, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            String username = jwtTokenUtil.extractUsername(token);
            return userRepository.existsByName(username) && jwtTokenUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads user details for Spring Security authentication.
     * Can find users by either username or email.
     *
     * @param identifier Username or email to look up
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByNameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName().toString()))
        );
    }
}