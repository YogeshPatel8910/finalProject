package com.example.finalProject.repository;

import com.example.finalProject.model.Appointment;
import com.example.finalProject.model.EStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    Page<Appointment> findAllByPatientNameAndStatusIn(String name, Collection<EStatus> status, Pageable pageable);

    Page<Appointment> findAllByDoctorNameAndStatusIn(String name, List<EStatus> status, Pageable pageable);

    Page<Appointment> findAllByDoctorName(String name, Pageable pageable);
    Page<Appointment> findAllByPatientName(String name, Pageable pageable);
}
