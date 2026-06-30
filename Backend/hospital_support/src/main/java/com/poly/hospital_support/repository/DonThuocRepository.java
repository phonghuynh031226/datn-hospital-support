package com.poly.hospital_support.repository;


import com.poly.hospital_support.entity.DonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonThuocRepository extends JpaRepository<DonThuoc, Integer> {
    // Spring Data JPA tự động tạo câu truy vấn dựa trên tên phương thức
    DonThuoc findByBenhAnId(Integer benhAnId);
}
