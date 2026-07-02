package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.NhapKhoThuoc;
import com.poly.hospital_support.repository.NhapKhoThuocRepository;
import com.poly.hospital_support.service.NhapKhoThuocService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NhapKhoThuocServiceImpl implements NhapKhoThuocService {

    private final NhapKhoThuocRepository nhapKhoThuocRepository;

    @Override
    public List<NhapKhoThuoc> findAll() {
        return nhapKhoThuocRepository.findAll();
    }

    @Override
    public Optional<NhapKhoThuoc> findById(Integer id) {
        return nhapKhoThuocRepository.findById(id);
    }

    @Override
    public NhapKhoThuoc save(NhapKhoThuoc nhapKhoThuoc) {
        return nhapKhoThuocRepository.save(nhapKhoThuoc);
    }

    @Override
    public NhapKhoThuoc update(NhapKhoThuoc nhapKhoThuoc) {
        return nhapKhoThuocRepository.save(nhapKhoThuoc);
    }

    @Override
    public void deleteById(Integer id) {
        nhapKhoThuocRepository.deleteById(id);
    }

    @Override
    public Optional<NhapKhoThuoc> findByMaPhieu(String maPhieu) {
        return nhapKhoThuocRepository.findByMaPhieu(maPhieu);
    }
}