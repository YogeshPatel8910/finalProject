package com.example.finalProject.controller;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.BranchDTO;
import com.example.finalProject.dto.DepartmentDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.ERole;
import com.example.finalProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for administrative operations.
 * Handles endpoints related to user management, departments, branches, and appointments.
 * Protected by ADMIN role through security configuration.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    /**
     * Retrieves all users with pagination, sorting, and optional role filtering.
     */
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllUser(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "search", required = false) ERole search) {

        Page<UserDTO> users = adminService.getAllUsers(page, size, sortBy, direction, search);
        Map<String, Object> response = new HashMap<>();
        response.put("data", users.getContent());
        response.put("TotalElements", users.getTotalElements());
        response.put("NumberOfElements", users.getNumberOfElements());
        response.put("pageNumber", users.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a specific user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id") long id) {
        UserDTO user = adminService.getById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Deletes a user by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable(name = "id") long id) {
        boolean isDeleted = adminService.deleteById(id);
        if (isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieves all departments with pagination and sorting.
     */
    @GetMapping("/department")
    public ResponseEntity<Map<String, Object>> getAllDepartments(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {

        Page<DepartmentDTO> departments = departmentService.getAllDepartments(page, size, sortBy, direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data", departments.getContent());
        response.put("TotalElements", departments.getTotalElements());
        response.put("NumberOfElements", departments.getNumberOfElements());
        response.put("pageNumber", departments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Creates a new department.
     */
    @PostMapping("/department")
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        return new ResponseEntity<>(departmentService.createDepartment(departmentDTO), HttpStatus.OK);
    }

    /**
     * Retrieves a specific department by ID.
     */
    @GetMapping("/department/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable(name = "name") long id) {
        DepartmentDTO department = departmentService.getById(id);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    /**
     * Updates an existing department.
     */
    @PutMapping("/department/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable(name = "id") long id,
            @RequestBody DepartmentDTO departmentDTO) {
        return new ResponseEntity<>(departmentService.updateDepartment(id, departmentDTO), HttpStatus.OK);
    }

    /**
     * Deletes a department by ID.
     */
    @DeleteMapping("/department/{id}")
    public ResponseEntity<HttpStatus> deleteDepartment(@PathVariable(name = "id") long id) {
        boolean isDeleted = departmentService.deleteDepartment(id);
        if (isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieves current appointments for admin with pagination and sorting.
     */
    @GetMapping("/appointment")
    public ResponseEntity<Map<String, Object>> getCurrentAppointments(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            Authentication authentication) {

        Page<AppointmentDTO> appointments = appointmentService.getCurrentAppointmentsForAdmin(
                authentication.getName(), page, size, sortBy, direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data", appointments.getContent());
        response.put("TotalElements", appointments.getTotalElements());
        response.put("NumberOfElements", appointments.getNumberOfElements());
        response.put("pageNumber", appointments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all branches with pagination and sorting.
     */
    @GetMapping("/branch")
    public ResponseEntity<Map<String, Object>> getAllBranch(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {

        Page<BranchDTO> departments = branchService.getAllBranch(page, size, sortBy, direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data", departments.getContent());
        response.put("TotalElements", departments.getTotalElements());
        response.put("NumberOfElements", departments.getNumberOfElements());
        response.put("pageNumber", departments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Creates a new branch.
     */
    @PostMapping("/branch")
    public ResponseEntity<BranchDTO> createBrancnh(@RequestBody BranchDTO branchDTO) {
        return new ResponseEntity<>(branchService.createBranch(branchDTO), HttpStatus.OK);
    }

    /**
     * Retrieves a specific branch by ID.
     */
    @GetMapping("/branch/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable(name = "name") long id) {
        BranchDTO branchDTO = branchService.getById(id);
        return new ResponseEntity<>(branchDTO, HttpStatus.OK);
    }

    /**
     * Updates an existing branch.
     */
    @PutMapping("/branch/{id}")
    public ResponseEntity<BranchDTO> updateBranch(
            @PathVariable(name = "id") long id,
            @RequestBody BranchDTO branchDTO) {
        return new ResponseEntity<>(branchService.updateBranch(id, branchDTO), HttpStatus.OK);
    }

    /**
     * Deletes a branch by ID.
     */
    @DeleteMapping("/branch/{id}")
    public ResponseEntity<HttpStatus> deleteBranch(@PathVariable(name = "id") long id) {
        boolean isDeleted = branchService.deleteBranch(id);
        if (isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}