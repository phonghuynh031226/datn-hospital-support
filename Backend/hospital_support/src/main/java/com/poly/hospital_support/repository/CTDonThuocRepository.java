package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.CTDonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CTDonThuocRepository extends JpaRepository<CTDonThuoc, Integer> {
    // Tìm tất cả chi tiết thuốc của một đơn thuốc cụ thể
    List<CTDonThuoc> findByDonThuocId(Integer donThuocId);
}
