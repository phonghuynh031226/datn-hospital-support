package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.MedicineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, Integer> {

    List<MedicineBatch> findByMedicineId(Integer medicineId);

}