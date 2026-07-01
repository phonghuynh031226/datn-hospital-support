package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.NhanVien;
import com.poly.hospital_support.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByTaiKhoan(TaiKhoan taiKhoan);
    Optional<NhanVien> findByTaiKhoanId(Integer taiKhoanId);

    @Query("SELECT DISTINCT nv FROM NhanVien nv " +
           "JOIN nv.taiKhoan tk " +
           "LEFT JOIN BacSiKhoa bsk ON bsk.nhanVien.id = nv.id " +
           "WHERE tk.vaiTro = 'BAC_SI' " +
           "AND (:departmentId IS NULL OR bsk.khoa.id = :departmentId) " +
           "AND (:search IS NULL OR LOWER(nv.hoTen) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<NhanVien> findAllDoctorsFiltered(
            @Param("departmentId") Integer departmentId,
            @Param("search") String search);
}
