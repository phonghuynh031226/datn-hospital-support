package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.TinTuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TinTucRepository extends JpaRepository<TinTuc, Integer> {
    org.springframework.data.domain.Page<TinTuc> findByDanhMucIdAndTieuDeContainingIgnoreCase(Integer categoryId, String title, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<TinTuc> findByTieuDeContainingIgnoreCase(String title, org.springframework.data.domain.Pageable pageable);
    java.util.Optional<TinTuc> findBySlug(String slug);
}
