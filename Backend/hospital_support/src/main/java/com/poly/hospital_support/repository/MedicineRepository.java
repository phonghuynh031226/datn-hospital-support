package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    // Kế thừa JpaRepository giúp bạn có sẵn các hàm xử lý DB cơ bản như:
    // findAll(), findById(), save(), deleteById()... mà không cần tự viết mã SQL thuần.
}








