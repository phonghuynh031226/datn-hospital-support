package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.DanhMucFaq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucFaqRepository extends JpaRepository<DanhMucFaq, Integer> {
}
