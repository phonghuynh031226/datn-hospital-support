package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.NhanVien;
import com.poly.hospital_support.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByTaiKhoan(TaiKhoan taiKhoan);
    Optional<NhanVien> findByTaiKhoanId(Integer taiKhoanId);
}
