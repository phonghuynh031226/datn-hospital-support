package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.MedicineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, Integer> {
}