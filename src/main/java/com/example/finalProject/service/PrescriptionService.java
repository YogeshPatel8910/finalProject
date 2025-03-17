package com.example.finalProject.service;

import com.example.finalProject.dto.BranchDTO;
import com.example.finalProject.dto.DepartmentDTO;
import com.example.finalProject.dto.PrescriptionDTO;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import com.example.finalProject.model.ERole;
import com.example.finalProject.model.Prescription;
import com.example.finalProject.repository.PrescriptionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private ModelMapper mapper;

    public PrescriptionDTO getPrescriptionById(Long id) {
        return mapper.map(prescriptionRepository.findById(id),PrescriptionDTO.class);
    }

    public Page<PrescriptionDTO> getAllPrescription(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return prescriptionRepository.findAll(pageable).map(prescription -> mapper.map(prescription, PrescriptionDTO.class));
    }

    public List<PrescriptionDTO> createPrescriptions(List<PrescriptionDTO> prescriptionDTO) {
        List<Prescription> prescriptions = prescriptionDTO.stream().map(prescription->mapper.map(prescription,Prescription.class)).toList();
        List<Prescription> newPrescription = prescriptionRepository.saveAll(prescriptions);
        return newPrescription.stream().map(prescription -> mapper.map(prescription,PrescriptionDTO.class)).toList();
    }

    public PrescriptionDTO updatePrescription(long id, PrescriptionDTO prescriptionDTO) {
        Prescription prescription = prescriptionRepository.findById(id).orElse(null);
        if(prescription!=null){
            mapper.map(prescriptionDTO,prescription);
            return mapper.map(prescription, PrescriptionDTO.class);
        }
        else
            return null;
    }

    public boolean deletePrescription(long id) {
        boolean isPresent = prescriptionRepository.existsById(id);
        if(isPresent){
            prescriptionRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }
}

