package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.BenhNhan;
import com.poly.hospital_support.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BenhNhanRepository extends JpaRepository<BenhNhan, Integer> {
    Optional<BenhNhan> findByTaiKhoan(TaiKhoan taiKhoan);
    Optional<BenhNhan> findByTaiKhoanId(Integer taiKhoanId);
}
