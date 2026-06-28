package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.YeuCauLienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YeuCauLienHeRepository extends JpaRepository<YeuCauLienHe, Integer> {
}
