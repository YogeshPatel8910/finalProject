package com.example.finalProject.controller;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.MedicalReportDTO;
import com.example.finalProject.service.AppointmentService;
import com.example.finalProject.service.DoctorService;
import com.example.finalProject.service.MedicalReportService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/doctor/profile")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MedicalReportService medicalReportService;

    @PutMapping("/availability")
    public ResponseEntity<DoctorDTO> setAvailability(Authentication authentication,
                                                     @RequestBody Set<LocalDate> dates) {
        DoctorDTO doctor = doctorService.setDate(authentication.getName(), dates);
        return new ResponseEntity<>(doctor,HttpStatus.OK);
    }

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDTO>> getCurrentAppointments(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                       @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                       @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                       @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                       Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getCurrentAppointmentsForDoctor(authentication.getName(),page,size,sortBy,direction);
        return new ResponseEntity<>(appointments.getContent(),HttpStatus.OK);
    }
    @GetMapping("/appointment/history")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentHistory(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                      @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                      @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                      @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                      Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getAppointmentHistoryForDoctor(authentication.getName(),page,size,sortBy,direction);
        return new ResponseEntity<>(appointments.getContent(),HttpStatus.OK);
    }
    @PutMapping("/appointment/{id}/cancel")
    @Transactional
    public ResponseEntity<AppointmentDTO> confirmAppointment(@PathVariable(name = "id")long id,
                                                            Authentication authentication) {
        boolean isDeleted = appointmentService.confirmAppointment(authentication.getName(),id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/appointment/{id}/noShow")
    @Transactional
    public ResponseEntity<AppointmentDTO> noShowAppointment(@PathVariable(name = "id")long id,
                                                             Authentication authentication) {
        boolean isDeleted = appointmentService.noShowAppointment(authentication.getName(),id);
        if(isDeleted)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/appointment/{id}")
    public ResponseEntity<MedicalReportDTO> createReport(@PathVariable(name = "id")long id,
                                                         @RequestBody MedicalReportDTO medicalReportDTO,
                                                         Authentication authentication) {
            MedicalReportDTO medicalReport = medicalReportService.createReport(id,authentication.getName(),medicalReportDTO);
            return new ResponseEntity<>(medicalReport, HttpStatus.OK);
    }



}
