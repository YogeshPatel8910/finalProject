package com.example.finalProject.repository;

import com.example.finalProject.model.ERole;
import com.example.finalProject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByName(String username);
    void deleteByName(String name);

    Optional<User> findByName(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByNameOrEmail(String name,String email);

    Page<User> findAllByRoleName(ERole search, Pageable pageable);
}
