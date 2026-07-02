package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {

    List<Medicine> findByTenThuocContainingIgnoreCase(String keyword);

}