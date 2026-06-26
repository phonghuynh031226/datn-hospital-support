package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.Khoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhoaRepository extends JpaRepository<Khoa, Integer> {
    java.util.List<Khoa> findByTenKhoaContainingIgnoreCase(String keyword);
    java.util.Optional<Khoa> findBySlug(String slug);
}
