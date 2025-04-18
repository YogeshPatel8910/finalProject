package com.example.finalProject.service;

import com.example.finalProject.dto.BranchDTO;
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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing Branch entities
 * Provides CRUD operations and other business logic for branches
 */
@Service
public class BranchService {

    // ModelMapper for entity to DTO conversion and vice versa
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Retrieves all branches with pagination and sorting
     *
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @return Page of BranchDTO objects
     */
    public Page<BranchDTO> getAllBranch(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return branchRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * Creates a new branch with associated departments
     *
     * @param branchDTO Branch data transfer object
     * @return Created branch as DTO, or null if name already exists
     */
    @Transactional
    public BranchDTO createBranch(BranchDTO branchDTO) {
        // Find all departments by name and filter out any null results
        Set<Department> departments = branchDTO.getDepartmentName().stream()
                .map(departmentRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Check if branch with the same name already exists
        if(!branchRepository.existsByName(branchDTO.getName())) {
            Branch branch = mapper.map(branchDTO, Branch.class);
            branch.setDepartment(departments);
            Branch newBranch = branchRepository.save(branch);
            return convertToDTO(newBranch);
        } else {
            return null;
        }
    }

    /**
     * Finds branch by ID
     *
     * @param id Branch ID
     * @return Branch DTO if found
     */
    public BranchDTO getById(long id) {
        return mapper.map(branchRepository.findById(id), BranchDTO.class);
    }

    /**
     * Updates an existing branch
     *
     * @param id Branch ID to update
     * @param branchDTO Updated branch data
     * @return Updated branch as DTO, or null if not found
     */
    @Transactional
    public BranchDTO updateBranch(long id, BranchDTO branchDTO) {
        Branch branch = branchRepository.findById(id).orElse(null);

        // Find all departments by name and filter out any null results
        Set<Department> departments = branchDTO.getDepartmentName().stream()
                .map(departmentRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if(branch != null) {
            branch.setName(branchDTO.getName());
            branch.setAddress(branchDTO.getAddress());
            branch.setPhone(branchDTO.getPhone());

            // Update departments efficiently - keep only the departments that exist in the updated list
            branch.getDepartment().retainAll(departments);
            branch.getDepartment().addAll(departments);

            return convertToDTO(branchRepository.save(branch));
        } else {
            return null;
        }
    }

    /**
     * Converts Branch entity to BranchDTO
     *
     * @param branch Branch entity
     * @return BranchDTO with department names
     */
    private BranchDTO convertToDTO(Branch branch) {
        BranchDTO branchDTO = mapper.map(branch, BranchDTO.class);
        branchDTO.setDepartmentName(branch.getDepartment().stream().map(Department::getName).toList());
        return branchDTO;
    }

    /**
     * Deletes a branch by ID
     *
     * @param id Branch ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteBranch(long id) {
        boolean isPresent = branchRepository.existsById(id);
        if(isPresent) {
            branchRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finds branch by name
     *
     * @param name Branch name
     * @return Branch DTO if found
     */
    public BranchDTO getByName(String name) {
        return mapper.map(branchRepository.findByName(name), BranchDTO.class);
    }

    /**
     * Gets all branches (without pagination)
     *
     * @return List of all branches
     */
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }
}