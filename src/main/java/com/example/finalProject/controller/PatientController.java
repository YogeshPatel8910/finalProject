//package com.example.finalProject.controller;
//
//import com.example.finalProject.dto.DoctorDTO;
//import com.example.finalProject.dto.PatientDTO;
//import com.example.finalProject.service.DoctorService;
//import com.example.finalProject.service.PatientService;
//import org.hibernate.collection.spi.PersistentBag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.crypto.spec.PSource;
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/patient")
//public class PatientController {
//
//    @Autowired
//    private PatientService patientService;
//
//    @GetMapping
//    public ResponseEntity<List<PatientDTO>> getAllUsers(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
//                                                        @RequestParam(name = "size",required = false,defaultValue = "10")int size,
//                                                        @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
//                                                        @RequestParam(name = "direction", defaultValue = "asc") String direction) {
//        Page<PatientDTO> doctors = patientService.getAllUsers(page,size,sortBy,direction);
//        return new ResponseEntity<>(doctors.getContent(), HttpStatus.OK);
//    }
//
//}