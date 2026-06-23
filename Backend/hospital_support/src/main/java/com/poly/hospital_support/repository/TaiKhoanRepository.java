package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findBySoDienThoai(String soDienThoai);
    Optional<TaiKhoan> findByEmail(String email);
    Optional<TaiKhoan> findBySoDienThoaiOrEmail(String soDienThoai, String email);
}
