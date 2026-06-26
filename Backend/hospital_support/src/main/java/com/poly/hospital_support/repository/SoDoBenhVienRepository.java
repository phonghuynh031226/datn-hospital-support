package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.SoDoBenhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoDoBenhVienRepository extends JpaRepository<SoDoBenhVien, Integer> {
}
