package com.example.finalProject.repository;

import com.example.finalProject.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch,Long> {
    Branch findByName(String name);
}
