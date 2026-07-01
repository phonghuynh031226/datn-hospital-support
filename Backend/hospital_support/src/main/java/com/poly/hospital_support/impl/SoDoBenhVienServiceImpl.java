package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.SoDoBenhVien;
import com.poly.hospital_support.repository.SoDoBenhVienRepository;
import com.poly.hospital_support.service.SoDoBenhVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SoDoBenhVienServiceImpl implements SoDoBenhVienService {

    private final SoDoBenhVienRepository soDoBenhVienRepository;

    @Override
    public List<SoDoBenhVien> getHospitalMaps() {
        return soDoBenhVienRepository.findAll();
    }
}
