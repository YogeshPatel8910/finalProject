package com.example.finalProject.controller;

import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.ERole;
import com.example.finalProject.service.UserFactory;
import com.example.finalProject.service.UserService;
import com.example.finalProject.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/{role}")
public class UserController {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUser(@PathVariable(name = "role")String role,
                                                    @RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                    @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                    @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));
        Page<UserDTO> users = userService.getAllUsers(page,size,sortBy,direction);
        return new ResponseEntity<>(users.getContent(), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserDTO> getAllUser(@RequestHeader("Authorization") String token,
                                              @PathVariable(name = "role")String role,
                                              @PathVariable(name = "name")String name){
        if(!jwtTokenUtil.extractUsername(token.substring(7)).equals(name))
            return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
        UserService userService = userFactory.getService(ERole.valueOf("ROLE_"+role.toUpperCase()));
        UserDTO users = userService.getByName(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
