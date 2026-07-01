package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.TinTuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TinTucService {
    Page<TinTuc> getNews(Integer categoryId, String search, Pageable pageable);
    TinTuc getNewsById(Integer id);
    TinTuc getNewsBySlug(String slug);
}
