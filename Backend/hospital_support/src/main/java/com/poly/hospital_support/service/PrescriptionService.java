package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.Prescription;
import com.poly.hospital_support.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository repository;

    public Prescription update(Integer id, Prescription prescription) {
        Prescription existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found id: " + id));

        existing.setDoctorId(prescription.getDoctorId());
        existing.setPatientId(prescription.getPatientId());
        existing.setNote(prescription.getNote());
        existing.setStatus(prescription.getStatus());

        return repository.save(existing);
    }
}