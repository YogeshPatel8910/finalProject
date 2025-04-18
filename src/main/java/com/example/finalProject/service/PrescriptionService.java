package com.example.finalProject.service;

import com.example.finalProject.dto.PrescriptionDTO;
import com.example.finalProject.model.Prescription;
import com.example.finalProject.repository.PrescriptionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for prescription operations
 * Handles CRUD operations for prescriptions
 */
@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // This needs to be initialized - should be @Autowired
    private ModelMapper mapper;

    /**
     * Gets a prescription by ID
     *
     * @param id Prescription ID
     * @return The prescription as DTO
     */
    public PrescriptionDTO getPrescriptionById(Long id) {
        return mapper.map(prescriptionRepository.findById(id), PrescriptionDTO.class);
    }

    /**
     * Gets all prescriptions with pagination and sorting
     *
     * @param page Page number
     * @param size Page size
     * @param sortBy Field to sort by
     * @param direction Sort direction (asc/desc)
     * @return Page of prescription DTOs
     */
    public Page<PrescriptionDTO> getAllPrescription(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return prescriptionRepository.findAll(pageable)
                .map(prescription -> mapper.map(prescription, PrescriptionDTO.class));
    }

    /**
     * Creates multiple prescriptions
     *
     * @param prescriptionDTO List of prescription DTOs
     * @return List of created prescription DTOs
     */
    public List<PrescriptionDTO> createPrescriptions(List<PrescriptionDTO> prescriptionDTO) {
        // Convert DTOs to entities
        List<Prescription> prescriptions = prescriptionDTO.stream()
                .map(prescription -> mapper.map(prescription, Prescription.class))
                .toList();

        // Save all prescriptions
        List<Prescription> newPrescription = prescriptionRepository.saveAll(prescriptions);

        // Convert entities back to DTOs
        return newPrescription.stream()
                .map(prescription -> mapper.map(prescription, PrescriptionDTO.class))
                .toList();
    }

    /**
     * Updates a prescription by ID
     *
     * @param id Prescription ID
     * @param prescriptionDTO Updated prescription data
     * @return Updated prescription as DTO, or null if not found
     */
    public PrescriptionDTO updatePrescription(long id, PrescriptionDTO prescriptionDTO) {
        Prescription prescription = prescriptionRepository.findById(id).orElse(null);
        if (prescription != null) {
            mapper.map(prescriptionDTO, prescription);
            return mapper.map(prescription, PrescriptionDTO.class);
        } else {
            return null;
        }
    }

    /**
     * Deletes a prescription by ID
     *
     * @param id Prescription ID
     * @return True if deleted, false if not found
     */
    public boolean deletePrescription(long id) {
        boolean isPresent = prescriptionRepository.existsById(id);
        if (isPresent) {
            prescriptionRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}