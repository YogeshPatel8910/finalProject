package com.example.finalProject.service;

import com.example.finalProject.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    Page<UserDTO> getAllUsers(int page, int size, String sortBy, String direction);
    UserDTO getByName(String name);
    UserDTO updateByName(String name, UserDTO userDTO);
    boolean deleteByName(String name);
}
