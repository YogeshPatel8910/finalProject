package com.example.finalProject.controller;

import com.example.finalProject.dto.BranchDTO;
import com.example.finalProject.dto.DepartmentDTO;
import com.example.finalProject.service.AdminService;
import com.example.finalProject.service.BranchService;
import com.example.finalProject.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private BranchService branchService;


    @GetMapping("/department")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                    @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                    @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        Page<DepartmentDTO> departments = departmentService.getAllDepartments(page,size,sortBy,direction);
        return new ResponseEntity<>(departments.getContent(), HttpStatus.OK);
    }

    @PostMapping("/department")
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO){
        return new ResponseEntity<>(departmentService.createDepartment(departmentDTO),HttpStatus.OK);
    }


    @GetMapping("/department/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable(name = "name")long id){
        DepartmentDTO department = departmentService.getById(id);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @PutMapping("/department/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable(name = "id") long id, @RequestBody DepartmentDTO departmentDTO) {
        return new ResponseEntity<>(departmentService.updateDepartment(id, departmentDTO),HttpStatus.OK);
    }

    @DeleteMapping("/department/{id}")
    public ResponseEntity<HttpStatus> deleteDepartment(@PathVariable(name = "id")long id){
        boolean isDeleted = departmentService.deleteSubmission(id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/branch")
    public ResponseEntity<List<BranchDTO>> getAllBranch(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                 @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                 @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                 @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        Page<BranchDTO> departments = branchService.getAllBranch(page,size,sortBy,direction);
        return new ResponseEntity<>(departments.getContent(), HttpStatus.OK);
    }

    @PostMapping("/branch")
    public ResponseEntity<BranchDTO> createDepartment(@RequestBody BranchDTO branchDTO){
        return new ResponseEntity<>(branchService.createBranch(branchDTO),HttpStatus.OK);
    }

    @GetMapping("/branch/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable(name = "name")long id){
        BranchDTO branchDTO = branchService.getById(id);
        return new ResponseEntity<>(branchDTO, HttpStatus.OK);
    }

    @PutMapping("/branch/{id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable(name = "id") long id, @RequestBody BranchDTO branchDTO) {
        return new ResponseEntity<>(branchService.updateBranch(id, branchDTO),HttpStatus.OK);
    }

    @DeleteMapping("/branch/{id}")
    public ResponseEntity<HttpStatus> deleteBranch(@PathVariable(name = "id")long id){
        boolean isDeleted = branchService.deleteBranch(id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

};
