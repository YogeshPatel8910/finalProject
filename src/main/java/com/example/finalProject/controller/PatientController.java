package com.example.finalProject.controller;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.UserDTO;
import com.example.finalProject.service.AppointmentService;
import com.example.finalProject.service.PatientService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/appointment")
    public ResponseEntity<Map<String,Object>> getCurrentAppointments(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                       @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                       @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                       @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                       Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getCurrentAppointmentsForPatient(authentication.getName(),page,size,sortBy,direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data",appointments.getContent());
        response.put("TotalElements",appointments.getTotalElements());
        response.put("NumberOfElements",appointments.getNumberOfElements());
        response.put("pageNumber",appointments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/appointment/history")
    public ResponseEntity<Map<String,Object>> getAppointmentHistory(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                       @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                       @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                       @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                       Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getAppointmentHistoryForPatient(authentication.getName(),page,size,sortBy,direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data",appointments.getContent());
        response.put("TotalElements",appointments.getTotalElements());
        response.put("NumberOfElements",appointments.getNumberOfElements());
        response.put("pageNumber",appointments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/appointment")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO,
                                                            Authentication authentication) {
        AppointmentDTO appointment = appointmentService.createAppointment(authentication.getName(),appointmentDTO);
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @PutMapping("/appointment/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable(name = "id")long id,
                                                            Authentication authentication) {
        boolean isDeleted = appointmentService.cancelAppointment(authentication.getName(),id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/appointment/{id}/reschedule")
    public ResponseEntity<AppointmentDTO> rescheduleAppointment(@PathVariable(name = "id")long id,
                                                            @RequestBody AppointmentDTO appointmentDTO,
                                                            Authentication authentication) {
        boolean isDeleted = appointmentService.rescheduleAppointment(authentication.getName(),id,appointmentDTO);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/data")
    public ResponseEntity<Object> getData() {
        List<Object> data = patientService.getData();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}