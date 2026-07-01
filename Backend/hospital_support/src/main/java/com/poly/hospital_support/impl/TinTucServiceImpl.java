package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.TinTuc;
import com.poly.hospital_support.repository.TinTucRepository;
import com.poly.hospital_support.service.TinTucService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TinTucServiceImpl implements TinTucService {

    private final TinTucRepository tinTucRepository;

    @Override
    public Page<TinTuc> getNews(Integer categoryId, String search, Pageable pageable) {
        String keyword = search != null ? search.trim() : "";
        if (categoryId != null) {
            return tinTucRepository.findByDanhMucIdAndTieuDeContainingIgnoreCase(categoryId, keyword, pageable);
        }
        return tinTucRepository.findByTieuDeContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public TinTuc getNewsById(Integer id) {
        return tinTucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại!"));
    }

    @Override
    public TinTuc getNewsBySlug(String slug) {
        return tinTucRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại!"));
    }
}
