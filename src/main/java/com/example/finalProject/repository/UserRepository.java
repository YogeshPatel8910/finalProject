package com.example.finalProject.repository;

import com.example.finalProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByName(String username);
    Optional<User> findByName(String username);
}
