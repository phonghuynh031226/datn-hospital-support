package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.Prescription;
import com.poly.hospital_support.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class PrescriptionService {

    @Autowired
    private PrescriptionRepository repository;

    public Prescription update(Long id, Prescription prescription) {

        Prescription existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found id: " + id));

        existing.setPatientName(prescription.getPatientName());
        existing.setItems(prescription.getItems());

        return repository.save(existing);
    }
}