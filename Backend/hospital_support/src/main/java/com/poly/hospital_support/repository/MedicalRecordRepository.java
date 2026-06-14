package com.poly.hospital_support.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecordRepository,Integer> {
}