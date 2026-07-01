package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.ChiTietDonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChiTietDonThuocRepository extends JpaRepository<ChiTietDonThuoc, Integer> {
    List<ChiTietDonThuoc> findByDonThuocId(Integer donThuocId);
}
