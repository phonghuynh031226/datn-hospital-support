package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.ThongTinLienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThongTinLienHeRepository extends JpaRepository<ThongTinLienHe, Integer> {
}
