package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.BenhAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenhAnRepository extends JpaRepository<BenhAn, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT ba FROM BenhAn ba " +
           "WHERE ba.benhNhan.id = :patientId " +
           "AND (:search IS NULL OR LOWER(ba.chanDoan) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(ba.trieuChung) LIKE LOWER(CONCAT('%', :search, '%')))")
    java.util.List<BenhAn> findByPatientIdAndSearch(
            @org.springframework.data.repository.query.Param("patientId") Integer patientId,
            @org.springframework.data.repository.query.Param("search") String search);
}
