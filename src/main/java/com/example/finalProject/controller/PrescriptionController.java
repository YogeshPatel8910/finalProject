package com.example.finalProject.controller;

import com.example.finalProject.dto.DepartmentDTO;
import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.PrescriptionDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.model.ERole;
import com.example.finalProject.model.Prescription;
import com.example.finalProject.service.PrescriptionService;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping()
    public ResponseEntity<Map<String,Object>> getPrescription(@RequestParam(name = "page",defaultValue = "0")int page,
                                                             @RequestParam(name = "size",defaultValue = "10")int size,
                                                             @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                             @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        Page<PrescriptionDTO> prescriptions = prescriptionService.getAllPrescription(page,size,sortBy,direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data",prescriptions.getContent());
        response.put("TotalElements",prescriptions.getTotalElements());
        response.put("NumberOfElements",prescriptions.getNumberOfElements());
        response.put("pageNumber",prescriptions.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/id")
    public ResponseEntity<PrescriptionDTO> getPrescription(@PathVariable(name = "id")long id){
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(id);
        return new ResponseEntity<>(prescription, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<PrescriptionDTO> createPrescription(@RequestBody PrescriptionDTO prescriptionDTO){
        return new ResponseEntity<>(prescriptionService.createPrescription(prescriptionDTO),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(@PathVariable(name = "id")long id,
                                                              @RequestBody PrescriptionDTO prescriptionDTO) {
        PrescriptionDTO prescription = prescriptionService.updatePrescription(id,prescriptionDTO);
        return new ResponseEntity<>(prescription,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePrescription(@PathVariable(name = "id")long id){
        boolean isDeleted = prescriptionService.deletePrescription(id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
