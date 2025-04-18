package com.example.finalProject.controller;

import com.example.finalProject.model.MedicalReport;
import com.example.finalProject.repository.MedicalReportRepository;
import com.example.finalProject.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for PDF generation endpoints
 * Handles generation and download of medical report PDFs
 */
@RestController
@RequestMapping("/api/auth")
public class PdfController {

    private final PdfGeneratorService pdfGeneratorService;
    private final MedicalReportRepository medicalReportRepository;

    /**
     * Constructor for dependency injection
     *
     * @param pdfGeneratorService Service for generating PDF documents
     * @param medicalReportRepository Repository for accessing medical reports
     */
    @Autowired
    public PdfController(PdfGeneratorService pdfGeneratorService,
                         MedicalReportRepository medicalReportRepository) {
        this.pdfGeneratorService = pdfGeneratorService;
        this.medicalReportRepository = medicalReportRepository;
    }

    /**
     * Generates a PDF invoice for a medical report
     *
     * @param id ID of the medical report
     * @return PDF document as byte array or 404 if report not found
     */
    @GetMapping("/invoice/{id}")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable(name = "id") Long id) {
        // Find the medical report by ID
        MedicalReport medicalReport = medicalReportRepository.findById(id)
                .orElse(null);

        // Return 404 if medical report not found
        if (medicalReport == null) {
            return ResponseEntity.notFound().build();
        }

        // Generate PDF from the medical report
        byte[] pdfBytes = pdfGeneratorService.generateMedicalReport(medicalReport);

        // Return PDF as downloadable attachment
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=medical-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}