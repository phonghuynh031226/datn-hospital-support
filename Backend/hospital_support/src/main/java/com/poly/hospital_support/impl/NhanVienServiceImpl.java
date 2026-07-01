package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.NhanVien;
import com.poly.hospital_support.repository.NhanVienRepository;
import com.poly.hospital_support.service.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;

    @Override
    public List<NhanVien> getAllDoctors(Integer departmentId, String search) {
        String keyword = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return nhanVienRepository.findAllDoctorsFiltered(departmentId, keyword);
    }

    @Override
    public NhanVien getDoctorById(Integer id) {
        return nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bác sĩ không tồn tại!"));
    }
}
