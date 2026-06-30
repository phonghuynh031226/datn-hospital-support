package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.CaiDatHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaiDatHeThongRepository extends JpaRepository<CaiDatHeThong, Integer> {
}