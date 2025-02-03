package com.example.finalProject.controller;

import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.ERole;
import com.example.finalProject.service.UserFactory;
import com.example.finalProject.service.UserService;
import com.example.finalProject.util.JwtTokenUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/{role}")
public class UserController {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN') or #name == authentication.name")
    public ResponseEntity<UserDTO> getUserByName(@RequestHeader("Authorization") String token,
                                              @PathVariable(name = "role")String role,
                                              @PathVariable(name = "name")String name){

        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));
        UserDTO users = userService.getByName(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @PutMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN') or #name == authentication.name")
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization") String token,
                                              @PathVariable(name = "role")String role,
                                              @PathVariable(name = "name")String name,
                                              @RequestBody UserDTO userDTO){
        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));
        UserDTO users = userService.updateByName(name,userDTO);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN') or #name == authentication.name")
    public ResponseEntity<HttpStatus> updateUser(@RequestHeader("Authorization") String token,
                                              @PathVariable(name = "role")String role,
                                              @PathVariable(name = "name")String name){
        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));
        boolean isDeleted = userService.deleteByName(name);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
