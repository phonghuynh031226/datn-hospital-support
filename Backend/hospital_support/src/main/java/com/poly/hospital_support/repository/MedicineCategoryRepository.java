package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineCategoryRepository extends JpaRepository<MedicineCategory, Integer> {
    // Kế thừa JpaRepository giúp bạn có sẵn các hàm cơ bản:
    // findAll(), findById(), save(), deleteById()... mà không cần viết SQL thuần.
}