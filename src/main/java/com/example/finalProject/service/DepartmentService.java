package com.example.finalProject.service;

import com.example.finalProject.dto.DepartmentDTO;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import com.example.finalProject.repository.BranchRepository;
import com.example.finalProject.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing Department entities
 * Provides CRUD operations and other business logic for departments
 */
@Service
public class DepartmentService {

    // ModelMapper for entity to DTO conversion and vice versa
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BranchRepository branchRepository;

    /**
     * Creates a new department with associated branches
     *
     * @param departmentDTO Department data transfer object
     * @return Created department as DTO, or null if name already exists
     */
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        // Find all branches by name and filter out any null results
        Set<Branch> branches = departmentDTO.getBranchName().stream()
                .map(branchRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Check if department with the same name already exists
        if(!departmentRepository.existsByName(departmentDTO.getName())){
            Department department = mapper.map(departmentDTO, Department.class);
            department.setBranch(branches);
            Department newDepartment = departmentRepository.save(department);
            return convertToDTO(newDepartment);
        } else {
            return null;
        }
    }

    /**
     * Updates an existing department
     *
     * @param id Department ID to update
     * @param departmentDTO Updated department data
     * @return Updated department as DTO, or null if not found
     */
    @Transactional
    public DepartmentDTO updateDepartment(long id, DepartmentDTO departmentDTO) {
        Department department = departmentRepository.findById(id).orElse(null);

        // Find all branches by name and filter out any null results
        Set<Branch> branches = departmentDTO.getBranchName().stream()
                .map(branchRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if(department != null) {
            department.setName(departmentDTO.getName());

            // Update branches efficiently - keep only the branches that exist in the updated list
            department.getBranch().retainAll(branches);
            department.getBranch().addAll(branches);

            return convertToDTO(departmentRepository.save(department));
        } else {
            return null;
        }
    }

    /**
     * Deletes a department by ID
     *
     * @param id Department ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteDepartment(long id) {
        boolean isPresent = departmentRepository.existsById(id);
        if(isPresent) {
            departmentRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves all departments with pagination and sorting
     *
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @return Page of DepartmentDTO objects
     */
    public Page<DepartmentDTO> getAllDepartments(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * Finds department by ID
     *
     * @param id Department ID
     * @return Department DTO if found
     */
    public DepartmentDTO getById(long id) {
        return mapper.map(departmentRepository.findById(id), DepartmentDTO.class);
    }

    /**
     * Finds department by name
     *
     * @param name Department name
     * @return Department DTO if found
     */
    public DepartmentDTO getByName(String name) {
        return mapper.map(departmentRepository.findByName(name), DepartmentDTO.class);
    }

    /**
     * Gets all departments (without pagination)
     *
     * @return List of all departments
     */
    public Object getAll() {
        return departmentRepository.findAll();
    }

    /**
     * Converts Department entity to DepartmentDTO
     *
     * @param department Department entity
     * @return DepartmentDTO with branch names
     */
    public DepartmentDTO convertToDTO(Department department){
        DepartmentDTO departmentDTO = mapper.map(department, DepartmentDTO.class);
        departmentDTO.setBranchName(department.getBranch().stream().map(Branch::getName).toList());
        return departmentDTO;
    }
}