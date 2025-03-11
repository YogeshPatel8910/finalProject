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
import java.util.List;
import java.util.Map;

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

    @GetMapping()
    public ResponseEntity<Map<String,Object>> getAllUser(@RequestParam(name = "page",defaultValue = "0")int page,
                                                    @RequestParam(name = "size",defaultValue = "10")int size,
                                                    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                    @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                    @RequestParam(name = "search",required = false) ERole search) {
        Page<UserDTO> users = adminService.getAllUsers(page,size,sortBy,direction,search);
        Map<String, Object> response = new HashMap<>();
        response.put("data",users.getContent());
        response.put("TotalElements",users.getTotalElements());
        response.put("NumberOfElements",users.getNumberOfElements());
        response.put("pageNumber",users.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id")long id){
        UserDTO user = adminService.getById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable(name = "id")long id){
        boolean isDeleted = adminService.deleteById(id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/department")
    public ResponseEntity<Map<String, Object>> getAllDepartments(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                    @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                    @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        Page<DepartmentDTO> departments = departmentService.getAllDepartments(page,size,sortBy,direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data",departments.getContent());
        response.put("TotalElements",departments.getTotalElements());
        response.put("NumberOfElements",departments.getNumberOfElements());
        response.put("pageNumber",departments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
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
        boolean isDeleted = departmentService.deleteDepartment(id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/appointment")
    public ResponseEntity<Map<String ,Object>> getCurrentAppointments(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                      @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                      @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                      @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                      Authentication authentication) {
        Page<AppointmentDTO> appointments = appointmentService.getCurrentAppointmentsForAdmin(authentication.getName(), page, size, sortBy, direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data", appointments.getContent());
        response.put("TotalElements", appointments.getTotalElements());
        response.put("NumberOfElements", appointments.getNumberOfElements());
        response.put("pageNumber", appointments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/branch")
    public ResponseEntity<Map<String,Object>> getAllBranch(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                 @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                 @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                 @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        Page<BranchDTO> departments = branchService.getAllBranch(page,size,sortBy,direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data",departments.getContent());
        response.put("TotalElements",departments.getTotalElements());
        response.put("NumberOfElements",departments.getNumberOfElements());
        response.put("pageNumber",departments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
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
        return new ResponseEntity<>(branchService.updateBranch(id, branchDTO), HttpStatus.OK);
    }

    @DeleteMapping("/branch/{id}")
    public ResponseEntity<HttpStatus> deleteBranch(@PathVariable(name = "id")long id){
        boolean isDeleted = branchService.deleteBranch(id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
//    @DeleteMapping("/branch/{id}")
//    public ResponseEntity<HttpStatus> deleteBranch(@PathVariable(name = "id")long id){
//        boolean isDeleted = branchService.deleteBranch(id);
//        if(isDeleted)
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        else
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    @DeleteMapping("/branch/{id}")
//    public ResponseEntity<HttpStatus> deleteBranch(@PathVariable(name = "id")long id){
//        boolean isDeleted = branchService.deleteBranch(id);
//        if(isDeleted)
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        else
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

}
