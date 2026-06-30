package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.DanhMucHuongDan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucHuongDanRepository extends JpaRepository<DanhMucHuongDan, Integer> {
}
