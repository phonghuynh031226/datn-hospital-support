package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.ViTriPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViTriPhongRepository extends JpaRepository<ViTriPhong, Integer> {
}
