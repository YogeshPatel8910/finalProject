package com.example.finalProject.controller;

import com.example.finalProject.dto.AppointmentDTO;
import com.example.finalProject.dto.DoctorDTO;
import com.example.finalProject.dto.MedicalReportDTO;
import com.example.finalProject.model.User;
import com.example.finalProject.repository.UserRepository;
import com.example.finalProject.service.*;
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
import java.util.Set;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MedicalReportService medicalReportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @PutMapping("/setLeave")
    public ResponseEntity<Set<LocalDate>> setLeave(Authentication authentication,
                                                     @RequestBody Set<LocalDate> dates) {
        Set<LocalDate> leave = doctorService.setDate(authentication.getName(), dates);
        return new ResponseEntity<>(leave,HttpStatus.OK);
    }
    @PutMapping("/deleteLeave")
    public ResponseEntity<Set<LocalDate>> deleteLeave(Authentication authentication,
                                                   @RequestBody Set<LocalDate> dates) {
        Set<LocalDate> leave = doctorService.deleteDate(authentication.getName(), dates);
        return new ResponseEntity<>(leave,HttpStatus.OK);
    }
    @GetMapping("/leave")
    public ResponseEntity<Set<LocalDate>> getLeave(Authentication authentication){
        Set<LocalDate> leave = doctorService.getDate(authentication.getName());
        return new ResponseEntity<>(leave,HttpStatus.OK);
    }

    @GetMapping("/appointment")
    public ResponseEntity<Map<String ,Object>> getCurrentAppointments(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                       @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                       @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                       @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                       Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getCurrentAppointmentsForDoctor(authentication.getName(),page,size,sortBy,direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data",appointments.getContent());
        response.put("TotalElements",appointments.getTotalElements());
        response.put("NumberOfElements",appointments.getNumberOfElements());
        response.put("pageNumber",appointments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/appointment/history")
    public ResponseEntity<Map<String ,Object>> getAppointmentHistory(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                                      @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                                                                      @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                      @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                      Authentication authentication){
        Page<AppointmentDTO> appointments = appointmentService.getAppointmentHistoryForDoctor(authentication.getName(),page,size,sortBy,direction);
        Map<String, Object> response = new HashMap<>();
        response.put("data",appointments.getContent());
        response.put("TotalElements",appointments.getTotalElements());
        response.put("NumberOfElements",appointments.getNumberOfElements());
        response.put("pageNumber",appointments.getNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/appointment/{id}/noShow")
    public ResponseEntity<AppointmentDTO> noShowAppointment(@PathVariable(name = "id")long id,
                                                             Authentication authentication) {
        boolean noShow = appointmentService.noShowAppointment(authentication.getName(),id);
        if(noShow){

            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/appointment/{id}/report")
    public ResponseEntity<MedicalReportDTO> createReport(@PathVariable(name = "id")long id,
                                                         @RequestBody MedicalReportDTO medicalReportDTO,
                                                         Authentication authentication) {

        MedicalReportDTO medicalReport = medicalReportService.createReport(id,authentication.getName(),medicalReportDTO);
        if(medicalReport != null){
            boolean isCompleted = appointmentService.completeAppointment(authentication.getName(),id);
            if(isCompleted){

                return new ResponseEntity<>(medicalReport,HttpStatus.OK);
            }
//          deleteMedicalReport
        }
        return new ResponseEntity<>(medicalReport, HttpStatus.OK);
    }

    @PutMapping("/appointment/{id}/reschedule")
    public ResponseEntity<AppointmentDTO> rescheduleAppointment(@PathVariable(name = "id")long id,
                                                                @RequestBody AppointmentDTO appointmentDTO,
                                                                Authentication authentication) {
        boolean isDeleted = appointmentService.rescheduleAppointment(authentication.getName(),id,appointmentDTO);
        if(isDeleted){

            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

//    @PostMapping("/appointment/{id}/report")
//    public ResponseEntity<AppointmentDTO> addReport(@PathVariable(name = "id")long id,
//                                                                @RequestBody MedicalReportDTO medicalReportDTO,
//                                                                Authentication authentication) {
//        boolean isDeleted = appointmentService.rescheduleAppointment(authentication.getName(),id,medicalReportDTO);
//        if(isDeleted)
//            return new ResponseEntity<>(HttpStatus.OK);
//        else
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

}
