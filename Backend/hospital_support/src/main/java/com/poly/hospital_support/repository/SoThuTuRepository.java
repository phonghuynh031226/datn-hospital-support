package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.SoThuTu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoThuTuRepository extends JpaRepository<SoThuTu, Integer> {
}
