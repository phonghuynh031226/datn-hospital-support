package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.ThanhToanLichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ThanhToanLichHenRepository extends JpaRepository<ThanhToanLichHen, Integer> {

    @Query("SELECT SUM(t.soTien) FROM ThanhToanLichHen t WHERE t.trangThai = :trangThai")
    BigDecimal sumSoTienByTrangThai(@Param("trangThai") String trangThai);

    @Query(value = "SELECT SUM(so_tien) FROM thanh_toan_lich_hen WHERE trang_thai = 'DA_THANH_TOAN' AND DATE(ngay_tao) BETWEEN :from AND :to", nativeQuery = true)
    BigDecimal tinhDoanhThuKhamTheoThoiGian(@Param("from") LocalDate from, @Param("to") LocalDate to);
}