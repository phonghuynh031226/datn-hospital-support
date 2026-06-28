package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.CauHoiThuongGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CauHoiThuongGapRepository extends JpaRepository<CauHoiThuongGap, Integer> {
}
