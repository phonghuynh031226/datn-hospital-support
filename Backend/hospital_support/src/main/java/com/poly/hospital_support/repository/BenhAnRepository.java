package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.BenhAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenhAnRepository extends JpaRepository<BenhAn, Integer> {

}
