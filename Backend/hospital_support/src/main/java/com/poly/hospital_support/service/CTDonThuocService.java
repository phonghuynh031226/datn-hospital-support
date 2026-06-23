package com.poly.hospital_support.service;



import com.poly.hospital_support.entity.CTDonThuoc;
import com.poly.hospital_support.repository.CTDonThuocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CTDonThuocService {
    @Autowired
    private CTDonThuocRepository ctDonThuocRepo;

    // Lấy chi tiết thuốc theo ID của đơn thuốc
    public List<CTDonThuoc> findByDonThuocId(Integer donThuocId) {
        return ctDonThuocRepo.findByDonThuocId(donThuocId);
    }

    public CTDonThuoc save(CTDonThuoc ctDonThuoc) {
        return ctDonThuocRepo.save(ctDonThuoc);
    }

    public void deleteById(Integer id) {
        ctDonThuocRepo.deleteById(id);
    }
}