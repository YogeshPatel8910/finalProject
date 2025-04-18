package com.example.finalProject.dto;

import com.example.finalProject.model.Activity;
import com.example.finalProject.model.ERole;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "roleName",
        visible = true,
        requireTypeIdForSubtypes = OptBoolean.FALSE
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserDTO.class,name = "ROLE_ADMIN"),
        @JsonSubTypes.Type(value = PatientDTO.class, name = "ROLE_PATIENT"),
        @JsonSubTypes.Type(value = DoctorDTO.class, name = "ROLE_DOCTOR"),
})
@Data
public class UserDTO {
    private Long id;

    private ERole roleName;

    private String name;

    private Long mobileNo;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Activity> activities;
}
