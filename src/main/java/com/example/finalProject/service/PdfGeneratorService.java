package com.example.finalProject.service;

import com.example.finalProject.model.MedicalReport;
import com.example.finalProject.model.Prescription;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGeneratorService {
    public byte[] generateMedicalReport(MedicalReport report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add Hospital Logo
            String imagePath = "src/main/resources/reports/invoice_logo.png";
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image logo = new Image(imageData);
            document.add(logo);

            // Title
            Paragraph title = new Paragraph("Medical Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20);
            document.add(title);

            // Patient Details
            document.add(new Paragraph("Patient Name: " + report.getPatient().getName()));
            document.add(new Paragraph("Branch: " + report.getAppointment().getBranch().getName()));
            document.add(new Paragraph("Doctor: " + report.getDoctor().getName()));
            document.add(new Paragraph("Department: " + report.getAppointment().getDepartment().getName()));
            document.add(new Paragraph("Reason: " + report.getAppointment().getReason()));
            document.add(new Paragraph("Date: " + report.getAppointment().getDate()));
            document.add(new Paragraph("Time Slot: " + report.getAppointment().getTimeSlot()));

            // Medical Report Details
            document.add(new Paragraph("\nSymptoms: " + report.getSymptom()));
            document.add(new Paragraph("Diagnosis: " + report.getDiagnosis()));
            document.add(new Paragraph("Notes: " + report.getNotes()));

            // Prescription Table
            Table table = new Table(new float[]{4, 2, 2, 4});
            table.addCell(new Cell().add(new Paragraph("Medicine Name")));
            table.addCell(new Cell().add(new Paragraph("Dosage")));
            table.addCell(new Cell().add(new Paragraph("Duration")));
            table.addCell(new Cell().add(new Paragraph("Instructions")));

            List<Prescription> prescriptions = report.getPrescriptions();
            for (Prescription prescription : prescriptions) {
                table.addCell(prescription.getName());
                table.addCell(prescription.getDosage());
                table.addCell(prescription.getDuration());
                table.addCell(prescription.getInstructions());
            }
            document.add(table);

            // Closing Remarks
            document.add(new Paragraph("\nStatus: " + report.getAppointment().getStatus()));
            document.add(new Paragraph("\nGet well soon!").setTextAlignment(TextAlignment.CENTER));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
