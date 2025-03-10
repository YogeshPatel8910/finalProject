package com.example.finalProject.service;

import com.example.finalProject.dto.BranchDTO;
import com.example.finalProject.dto.DepartmentDTO;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import com.example.finalProject.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    ModelMapper mapper = new ModelMapper();

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BranchService branchService;

    @Transactional
    public  DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        List<Branch> branches = departmentDTO.getBranchName().stream()
                .map(branchService::getByName)
                .filter(Objects::nonNull) // Remove null branches
                .toList();
        if(!departmentRepository.existsByName(departmentDTO.getName())){
            TypeMap<DepartmentDTO, Department> typeMap = mapper.typeMap(DepartmentDTO.class, Department.class);
            typeMap.addMappings(mapping -> mapping.skip(Department::setBranch));
            Department department = mapper.map(departmentDTO,Department.class);
            department.setBranch(branches);
            Department newDepartment = departmentRepository.save(department);
//            System.out.println(newDepartment);
            System.out.println("zazaz " +department);
            return mapper.map(newDepartment, DepartmentDTO.class);
        }else{
            System.out.println("qwqw");
            return null;
        }

    }

    @Transactional
    public DepartmentDTO updateDepartment(long id, DepartmentDTO departmentDTO) {
        Department department = departmentRepository.findById(id).orElse(null);
        List<Branch> branches = departmentDTO.getBranchName().stream()
                .map(branchService::getByName)
                .filter(Objects::nonNull) // Remove null branches
                .map(branch -> mapper.map(branch, Branch.class))
                .toList();
        if(department!=null){
            mapper.map(departmentDTO,department);
            return mapper.map(department,DepartmentDTO.class);
        }
        else
            return null;
    }

    public  boolean deleteDepartment(long id) {
        boolean isPresent = departmentRepository.existsById(id);
        if(isPresent){
            departmentRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }

    public Page<DepartmentDTO> getAllDepartments(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        return departmentRepository.findAll(pageable).map(department -> mapper.map(department, DepartmentDTO.class));
    }

    public DepartmentDTO getById(long id) {
        return mapper.map(departmentRepository.findById(id), DepartmentDTO.class);
    }

    public DepartmentDTO getByName(String name) {
        return mapper.map(departmentRepository.findByName(name),DepartmentDTO.class);
    }

    public Object getAll() {
        return departmentRepository.findAll();
    }
}
