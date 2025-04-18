package com.example.finalProject.repository;


import com.example.finalProject.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepsoitory extends JpaRepository<Activity, Long> {
}
