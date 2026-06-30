package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.TinTuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinTucRepository extends JpaRepository<TinTuc, Integer> {

    // Danh sách các bài đã ghim
    List<TinTuc> findByDaGhimTrue();

    // Đếm số bài đã ghim
    long countByDaGhimTrue();

    // Lấy tất cả tin tức mới nhất
    List<TinTuc> findAllByOrderByNgayTaoDesc();
}