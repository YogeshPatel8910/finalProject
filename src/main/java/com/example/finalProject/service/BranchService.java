package com.example.finalProject.service;

import com.example.finalProject.dto.BranchDTO;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import com.example.finalProject.repository.BranchRepository;
import com.example.finalProject.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BranchService {

    ModelMapper mapper =new ModelMapper();

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

//    public BranchService(@Lazy  DepartmentService departmentService) {
//        this.departmentService = departmentService;
//    }

    public Page<BranchDTO> getAllBranch(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        return branchRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional
    public BranchDTO createBranch(BranchDTO branchDTO) {
        Set<Department> departments = branchDTO.getDepartmentName().stream()
                .map(departmentRepository::findByName)
                .filter(Objects::nonNull)
//                .map(departmentDTO -> mapper.map(departmentDTO,Department.class))
                .collect(Collectors.toSet());
        if(!branchRepository.existsByName(branchDTO.getName())){
        Branch branch = mapper.map(branchDTO,Branch.class);
        branch.setDepartment(departments);
        Branch newBranch = branchRepository.save(branch);
        return convertToDTO(newBranch);
        }else {
            return null;
        }
    }

    public BranchDTO getById(long id) {
        return mapper.map(branchRepository.findById(id),BranchDTO.class);
    }

    @Transactional
    public BranchDTO updateBranch(long id, BranchDTO branchDTO) {
        Branch branch = branchRepository.findById(id).orElse(null);
        Set<Department> departments = branchDTO.getDepartmentName().stream()
                .map(departmentRepository::findByName)
                .filter(Objects::nonNull)
//                .map(departmentDTO -> mapper.map(departmentDTO,Department.class))
                .collect(Collectors.toSet());
        if(branch!=null){
            branch.setName(branchDTO.getName());
            branch.setAddress(branchDTO.getAddress());
            branch.setPhone(branch.getPhone());
            branch.getDepartment().retainAll(departments);
            branch.getDepartment().addAll(departments);
            return convertToDTO(branchRepository.save(branch));
        }
        else
            return null;
    }

    private BranchDTO convertToDTO(Branch branch) {
        BranchDTO branchDTO = mapper.map(branch,BranchDTO.class);
        branchDTO.setDepartmentName(branch.getDepartment().stream().map(Department::getName).toList());
        return branchDTO;
    }

    public boolean deleteBranch(long id) {
        boolean isPresent = branchRepository.existsById(id);
        if(isPresent){
            branchRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }

    public BranchDTO getByName(String name) {
        return mapper.map(branchRepository.findByName(name),BranchDTO.class);
    }

    public List<Branch> getAll() {
        return branchRepository.findAll();
    }
}
