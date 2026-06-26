package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.YeuCauDichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YeuCauDichVuRepository extends JpaRepository<YeuCauDichVu, Integer> {
}
