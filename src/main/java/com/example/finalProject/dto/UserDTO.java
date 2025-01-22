package com.example.finalProject.dto;

import com.example.finalProject.model.ERole;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {
    private Long id;

    private ERole role;

    private String name;

    private Long mobileNo;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
