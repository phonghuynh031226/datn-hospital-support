package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.ExaminationQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExaminationQueueRepository
        extends JpaRepository<ExaminationQueue, Long> {
}
