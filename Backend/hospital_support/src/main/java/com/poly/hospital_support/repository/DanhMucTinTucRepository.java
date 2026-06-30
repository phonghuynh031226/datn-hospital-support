package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.DanhMucTinTuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucTinTucRepository extends JpaRepository<DanhMucTinTuc, Integer> {
}
