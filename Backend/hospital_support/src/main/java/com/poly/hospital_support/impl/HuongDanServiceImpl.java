package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.HuongDan;
import com.poly.hospital_support.repository.HuongDanRepository;
import com.poly.hospital_support.service.HuongDanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HuongDanServiceImpl implements HuongDanService {

    private final HuongDanRepository huongDanRepository;

    @Override
    public List<HuongDan> getGuides(Integer categoryId, String search) {
        String keyword = search != null ? search.trim() : "";
        if (categoryId != null) {
            return huongDanRepository.findByDanhMucIdAndTieuDeContainingIgnoreCase(categoryId, keyword);
        }
        return huongDanRepository.findByTieuDeContainingIgnoreCase(keyword);
    }

    @Override
    public HuongDan getGuideById(Integer id) {
        return huongDanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài hướng dẫn không tồn tại!"));
    }
}
