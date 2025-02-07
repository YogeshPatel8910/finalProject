package com.example.finalProject.controller;

import com.example.finalProject.dto.AppointmentDTO;
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
import java.util.List;

@RestController
@RequestMapping("/api/patient/profile")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDTO>> getCurrentAppointments(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                       @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                       @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                       @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                       Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getCurrentAppointmentsForPatient(authentication.getName(),page,size,sortBy,direction);
        return new ResponseEntity<>(appointments.getContent(),HttpStatus.OK);
    }
    @GetMapping("/appointment/history")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentHistory(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                       @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                       @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                       @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                       Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getAppointmentHistoryForPatient(authentication.getName(),page,size,sortBy,direction);
        return new ResponseEntity<>(appointments.getContent(),HttpStatus.OK);
    }

    @PostMapping("/appointment")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO,
                                                            Authentication authentication) {
        AppointmentDTO appointment = appointmentService.createAppointment(authentication.getName(),appointmentDTO);
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @PutMapping("/appointment/{id}/cancel")
    @Transactional
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable(name = "id")long id,
                                                            Authentication authentication) {
        boolean isDeleted = appointmentService.cancelAppointment(authentication.getName(),id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/appointment/{id}/reschedule")
    @Transactional
    public ResponseEntity<AppointmentDTO> rescheduleAppointment(@PathVariable(name = "id")long id,
                                                            @RequestBody LocalDate date,
                                                            Authentication authentication) {
        boolean isDeleted = appointmentService.rescheduleAppointment(authentication.getName(),id,date);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}