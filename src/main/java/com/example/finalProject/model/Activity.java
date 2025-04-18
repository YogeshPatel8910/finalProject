package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    private String Name;

    private LocalDateTime time;

    private String status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;


}
