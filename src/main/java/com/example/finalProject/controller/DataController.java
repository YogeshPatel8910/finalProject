package com.example.finalProject.controller;

import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.response.DataDTO;
import com.example.finalProject.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth/data")
public class  DataController {

    @Autowired
    private PatientService patientService;

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
                        hospital -> hospital.getDepartment().stream()
                                .collect(Collectors.toMap(
                                        Department::getName,
                                        doctors -> hospital.getDoctors().stream()
                                                .map(Doctor::getName)
                                                .collect(Collectors.toList())
                                ))

                ));
        DataDTO dataDTO = new DataDTO();
        dataDTO.setData(response);
        return new ResponseEntity<>(dataDTO.getData()     , HttpStatus.OK);
    }
}
