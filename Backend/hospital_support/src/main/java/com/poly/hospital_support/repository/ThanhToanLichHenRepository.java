package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.ThanhToanLichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThanhToanLichHenRepository extends JpaRepository<ThanhToanLichHen, Integer> {
}
