package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.HuongDan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HuongDanRepository extends JpaRepository<HuongDan, Integer> {
    java.util.List<HuongDan> findByDanhMucIdAndTieuDeContainingIgnoreCase(Integer categoryId, String title);
    java.util.List<HuongDan> findByTieuDeContainingIgnoreCase(String title);
}
