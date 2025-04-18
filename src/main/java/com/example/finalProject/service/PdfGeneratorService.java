package com.example.finalProject.service;

import com.example.finalProject.model.MedicalReport;
import com.example.finalProject.model.Prescription;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Service for generating PDF documents
 * Creates medical report PDFs using iText library
 */
@Service
public class PdfGeneratorService {

    /**
     * Generates a PDF medical report from a MedicalReport entity
     *
     * @param report The medical report entity
     * @return Byte array containing the generated PDF
     */
    public byte[] generateMedicalReport(MedicalReport report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Initialize PDF writer and document
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add Hospital Logo
            String imagePath = "src/main/resources/reports/invoice_logo.png";
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image logo = new Image(imageData);
            document.add(logo);

            // Add Title
            Paragraph title = new Paragraph("Medical Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20);
            document.add(title);

            // Add Patient Details section
            document.add(new Paragraph("Patient Name: " + report.getPatient().getName()));
            document.add(new Paragraph("Branch: " + report.getAppointment().getBranch().getName()));
            document.add(new Paragraph("Doctor: " + report.getDoctor().getName()));
            document.add(new Paragraph("Department: " + report.getAppointment().getDepartment().getName()));
            document.add(new Paragraph("Reason: " + report.getAppointment().getReason()));
            document.add(new Paragraph("Date: " + report.getAppointment().getDate()));
            document.add(new Paragraph("Time Slot: " + report.getAppointment().getTimeSlot()));

            // Add Medical Report Details section
            document.add(new Paragraph("\nSymptoms: " + report.getSymptom()));
            document.add(new Paragraph("Diagnosis: " + report.getDiagnosis()));
            document.add(new Paragraph("Notes: " + report.getNotes()));

            // Add Prescription Table
            Table table = new Table(new float[]{4, 2, 2, 4});
            // Add table headers
            table.addCell(new Cell().add(new Paragraph("Medicine Name")));
            table.addCell(new Cell().add(new Paragraph("Dosage")));
            table.addCell(new Cell().add(new Paragraph("Duration")));
            table.addCell(new Cell().add(new Paragraph("Instructions")));

            // Add prescription data to table
            List<Prescription> prescriptions = report.getPrescriptions();
            for (Prescription prescription : prescriptions) {
                table.addCell(prescription.getName());
                table.addCell(prescription.getDosage());
                table.addCell(prescription.getDuration());
                table.addCell(prescription.getInstructions());
            }
            document.add(table);

            // Add Closing section
            document.add(new Paragraph("\nStatus: " + report.getAppointment().getStatus()));
            document.add(new Paragraph("\nGet well soon!").setTextAlignment(TextAlignment.CENTER));

            // Close document and return PDF as byte array
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}