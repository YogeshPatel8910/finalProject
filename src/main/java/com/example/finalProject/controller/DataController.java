package com.example.finalProject.controller;

import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.response.DataDTO;
import com.example.finalProject.service.DoctorService;
import com.example.finalProject.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth/data")
public class  DataController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/register")
    public ResponseEntity<Object> getRegisterData() {
        List<Branch> data = patientService.getData();
        Map<String,  List<String>> response = data.stream()
                .collect(Collectors.toMap(
                        Branch::getName,
                        hospital -> hospital.getDepartment().stream()
                                .map(Department::getName)
                                .collect(Collectors.toList())

                ));
        DataDTO dataDTO = new DataDTO();
        dataDTO.setData(response);
        return new ResponseEntity<>(dataDTO.getData()   , HttpStatus.OK);
    }
    @GetMapping("/appointment")
    public ResponseEntity<Object> getAppointmentData() {
        List<Branch> data = patientService.getData();
        Map<String,Map<String, List<String>>> response = data.stream()
                .collect(Collectors.toMap(
                        Branch::getName,
                        branch -> branch.getDepartment().stream()
                                .collect(Collectors.toMap(
                                        Department::getName,
                                        doctors -> doctors.getDoctors()
                                                .stream()
                                                .filter(doctor ->
                                                        doctor.getBranch().getName().equals(branch.getName()))
                                                .map(Doctor::getName)
                                                .collect(Collectors.toList())
                                ))

                ));
        DataDTO dataDTO = new DataDTO();
        dataDTO.setData(response);
        return new ResponseEntity<>(dataDTO.getData(), HttpStatus.OK);
    }
    @GetMapping("/leave/{name}")
    public ResponseEntity<Set<LocalDate>> getLeave(@PathVariable(name = "name")String name){
        Set<LocalDate> leave = doctorService.getDate(name);
        return new ResponseEntity<>(leave,HttpStatus.OK);
    }
}
