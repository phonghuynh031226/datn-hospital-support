package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.Khoa;
import com.poly.hospital_support.repository.KhoaRepository;
import com.poly.hospital_support.service.KhoaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KhoaServiceImpl implements KhoaService {

    private final KhoaRepository khoaRepository;

    @Override
    public List<Khoa> getDepartments(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return khoaRepository.findByTenKhoaContainingIgnoreCase(search.trim());
        }
        return khoaRepository.findAll();
    }

    @Override
    public Khoa getDepartmentById(Integer id) {
        return khoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khoa không tồn tại!"));
    }

    @Override
    public Khoa getDepartmentBySlug(String slug) {
        return khoaRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Khoa không tồn tại!"));
    }
}
