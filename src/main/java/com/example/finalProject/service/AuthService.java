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

@Service
@Qualifier("authService")
public class AuthService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    ModelMapper mapper = new ModelMapper();

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String generateToken(String username, String roles) {
        return jwtTokenUtil.generateToken(username,roles);
    }

    public boolean validateToken(String token) {
        try {
            String username = jwtTokenUtil.extractUsername(token);
            return userRepository.existsByName(username) && jwtTokenUtil.validateToken(token) ;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName().toString()))
        );
    }


}
