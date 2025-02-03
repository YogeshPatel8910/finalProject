package com.example.finalProject.repository;

import com.example.finalProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByName(String username);

    User findByName(String username);

    void deleteByName(String name);
}
