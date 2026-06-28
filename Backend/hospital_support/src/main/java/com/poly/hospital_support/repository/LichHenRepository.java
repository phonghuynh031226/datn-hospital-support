package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.LichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LichHenRepository extends JpaRepository<LichHen, Integer> {
    List<LichHen> findByBenhNhanId(Integer benhNhanId);
    List<LichHen> findByBenhNhanTaiKhoanId(Integer taiKhoanId);
}
