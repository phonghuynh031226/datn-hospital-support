package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.DonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonThuocRepository extends JpaRepository<DonThuoc, Integer> {
    java.util.Optional<DonThuoc> findByBenhAnId(Integer benhAnId);
    List<DonThuoc> findByBenhNhanId(Integer patientId);
    List<DonThuoc> findByBenhNhanTaiKhoanId(Integer taiKhoanId);
}
