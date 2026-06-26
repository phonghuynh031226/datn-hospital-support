package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.BioBacSi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BioBacSiRepository extends JpaRepository<BioBacSi, Integer> {
    java.util.Optional<BioBacSi> findByTaiKhoanId(Integer taiKhoanId);
}
