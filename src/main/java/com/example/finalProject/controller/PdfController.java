package com.example.finalProject.controller;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.MedicalReport;
import com.example.finalProject.repository.MedicalReportRepository;
import com.example.finalProject.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class PdfController {

    private final PdfGeneratorService pdfGeneratorService;

    @Autowired
    private MedicalReportRepository medicalReportRepository;

    public PdfController(PdfGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable(name = "id")Long id) {

        MedicalReport medicalReport = medicalReportRepository.findById(id).orElse(null);
        byte[] pdfBytes = pdfGeneratorService.generateMedicalReport(medicalReport);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
