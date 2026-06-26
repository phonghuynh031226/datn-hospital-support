package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.BacSiKhoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BacSiKhoaRepository extends JpaRepository<BacSiKhoa, Integer> {
    List<BacSiKhoa> findByNhanVienId(Integer nhanVienId);

    @Query("SELECT bsk FROM BacSiKhoa bsk " +
           "JOIN FETCH bsk.nhanVien nv " +
           "WHERE bsk.khoa.id = :khoaId " +
           "AND LOWER(bsk.chucVu) LIKE LOWER('%Trưởng khoa%')")
    List<BacSiKhoa> findTruongKhoaByKhoaId(@Param("khoaId") Integer khoaId);
}
