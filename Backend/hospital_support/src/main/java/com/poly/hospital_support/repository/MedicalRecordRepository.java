package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    // File này xử lý cho Bệnh Án (MedicalRecord) nha!
}