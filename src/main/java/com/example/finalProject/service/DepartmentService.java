package com.example.finalProject.service;

import com.example.finalProject.dto.DepartmentDTO;
import com.example.finalProject.model.Department;
import com.example.finalProject.repository.DepartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    ModelMapper mapper = new ModelMapper();

    @Autowired
    private DepartmentRepository departmentRepository;

    public  DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Department department = mapper.map(departmentDTO,Department.class);
        Department newDepartment = departmentRepository.save(department);
        return mapper.map(newDepartment,DepartmentDTO.class);

    }

    public DepartmentDTO updateDepartment(long id, DepartmentDTO departmentDTO) {
            Department department = departmentRepository.findById(id).orElse(null);
            if(department!=null){
                mapper.map(departmentDTO,department);
                return mapper.map(department,DepartmentDTO.class);
            }
            else
                return null;

    }

    public  boolean deleteSubmission(long id) {
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
        Pageable pageable = PageRequest.of(page, size, sort).first().next().previous();
        return departmentRepository.findAll(pageable).map(department -> mapper.map(department, DepartmentDTO.class));
    }

    public DepartmentDTO getById(long id) {
        return mapper.map(departmentRepository.findById(id), DepartmentDTO.class);
    }

    public DepartmentDTO getByName(String name) {
        return mapper.map(departmentRepository.findByName(name),DepartmentDTO.class);
    }
}
