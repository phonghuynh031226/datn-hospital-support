package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.BenhAn;
import com.poly.hospital_support.repository.BenhAnRepository;
import com.poly.hospital_support.service.BenhAnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenhAnServiceImpl implements BenhAnService {

    private final BenhAnRepository benhAnRepository;

    @Override
    public List<BenhAn> getPatientMedicalHistory(Integer patientId, String search) {
        String keyword = search != null ? search.trim() : null;
        return benhAnRepository.findByPatientIdAndSearch(patientId, keyword);
    }

    @Override
    public BenhAn getMedicalRecordById(Integer id) {
        return benhAnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bệnh án không tồn tại!"));
    }
}
