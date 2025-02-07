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

@RestController
@RequestMapping("api/{role}")
public class UserController {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserByName(@PathVariable(name = "role")String role,
                                                 Authentication authentication){
        System.out.println(authentication);;
        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));
        UserDTO users = userService.getByName(authentication.getName());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateUser(@PathVariable(name = "role")String role,
                                              @RequestBody UserDTO userDTO,
                                              Authentication authentication){
        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));
        UserDTO users = userService.updateByName(authentication.getName(), userDTO);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @DeleteMapping("/profile")
    public ResponseEntity<HttpStatus> updateUser(@PathVariable(name = "role")String role,
                                                 Authentication authentication){
        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));

        boolean isDeleted = userService.deleteByName(authentication.getName());
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
