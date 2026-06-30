package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findBySoDienThoai(String soDienThoai);
    Optional<TaiKhoan> findByEmail(String email);
    Optional<TaiKhoan> findBySoDienThoaiOrEmail(String soDienThoai, String email);

    @Query(value = "SELECT ma_tai_khoan FROM nhan_vien WHERE id = :nhanVienId", nativeQuery = true)
    Integer findIdByNhanVienId(@Param("nhanVienId") Integer nhanVienId);

    @Modifying
    @Query("UPDATE TaiKhoan t SET t.trangThai = :trangThai WHERE t.id = :id")
    void updateTrangThaiByTaiKhoanId(@Param("id") Integer id, @Param("trangThai") Integer trangThai);
}