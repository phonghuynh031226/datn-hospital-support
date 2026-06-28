package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionItemRepository
        extends JpaRepository<PrescriptionItem, Long> {
}