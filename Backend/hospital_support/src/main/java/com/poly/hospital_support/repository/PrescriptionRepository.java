package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {
}