package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.DonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface DonThuocRepository extends JpaRepository<DonThuoc, Integer> {
    java.util.Optional<DonThuoc> findByBenhAnId(Integer benhAnId);
    List<DonThuoc> findByBenhNhanId(Integer patientId);
    List<DonThuoc> findByBenhNhanTaiKhoanId(Integer taiKhoanId);

    @Query("SELECT SUM(d.tongTien) FROM DonThuoc d WHERE d.trangThai = :trangThai")
    BigDecimal sumTongTienByTrangThai(@Param("trangThai") String trangThai);

    @Query(value = "SELECT SUM(tong_tien) FROM don_thuoc WHERE trang_thai = 'DA_PHAT_THUOC' AND DATE(ngay_tao) BETWEEN :from AND :to", nativeQuery = true)
    BigDecimal tinhDoanhThuThuocTheoThoiGian(@Param("from") LocalDate from, @Param("to") LocalDate to);
}