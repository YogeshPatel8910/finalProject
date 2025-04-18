package com.example.finalProject.controller;

import com.example.finalProject.dto.PrescriptionDTO;
import com.example.finalProject.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing prescription-related endpoints
 * Provides CRUD operations for prescriptions
 */
@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /**
     * Constructor for dependency injection
     *
     * @param prescriptionService Service for prescription operations
     */
    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    /**
     * Retrieves a paginated list of prescriptions
     *
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param sortBy Field to sort results by
     * @param direction Sort direction (asc/desc)
     * @return Paginated prescriptions with metadata
     */
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getPrescriptions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {

        Page<PrescriptionDTO> prescriptions = prescriptionService.getAllPrescription(
                page, size, sortBy, direction);

        Map<String, Object> response = new HashMap<>();
        response.put("data", prescriptions.getContent());
        response.put("totalElements", prescriptions.getTotalElements());
        response.put("numberOfElements", prescriptions.getNumberOfElements());
        response.put("pageNumber", prescriptions.getNumber());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a prescription by its ID
     *
     * @param id Prescription ID
     * @return Prescription details
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable(name = "id") long id) {
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(id);

        if (prescription == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(prescription, HttpStatus.OK);
    }

    /**
     * Updates an existing prescription
     *
     * @param id Prescription ID to update
     * @param prescriptionDTO Updated prescription details
     * @return Updated prescription
     */
    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
            @PathVariable(name = "id") long id,
            @RequestBody PrescriptionDTO prescriptionDTO) {

        PrescriptionDTO prescription = prescriptionService.updatePrescription(id, prescriptionDTO);

        if (prescription == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(prescription, HttpStatus.OK);
    }

    /**
     * Deletes a prescription by ID
     *
     * @param id Prescription ID to delete
     * @return No content if successful, Not found if prescription doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePrescription(@PathVariable(name = "id") long id) {
        boolean isDeleted = prescriptionService.deletePrescription(id);

        return isDeleted
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}