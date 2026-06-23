package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.CTDonThuoc;
import com.poly.hospital_support.entity.DonThuoc;
import com.poly.hospital_support.repository.CTDonThuocRepository;
import com.poly.hospital_support.repository.DonThuocRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonThuocService {

    @Autowired
    private DonThuocRepository donThuocRepo;

    @Autowired
    private CTDonThuocRepository ctDonThuocRepo;

    // 1. Lưu mới hoặc cập nhật đơn thuốc kèm chi tiết
    @Transactional
    public DonThuoc saveDonThuoc(DonThuoc donThuoc) {
        // Lưu thông tin đơn thuốc chính trước
        DonThuoc savedDon = donThuocRepo.save(donThuoc);

        // Nếu có danh sách thuốc, gán quan hệ và lưu từng chi tiết
        if (donThuoc.getCtDonThuocList() != null) {
            for (CTDonThuoc ct : donThuoc.getCtDonThuocList()) {
                ct.setDonThuoc(savedDon);
                ctDonThuocRepo.save(ct);
            }
        }
        return savedDon;
    }

    // 2. Tìm đơn thuốc theo ID (để Controller dùng)
    public Optional<DonThuoc> findById(Integer id) {
        return donThuocRepo.findById(id);
    }

    // 3. Lấy tất cả đơn thuốc
    public List<DonThuoc> findAll() {
        return donThuocRepo.findAll();
    }

    // 4. Tìm đơn thuốc theo mã bệnh án (như repo bạn đã viết)
    public DonThuoc findByBenhAnId(Integer benhAnId) {
        return donThuocRepo.findByBenhAnId(benhAnId);
    }

    // 5. Xóa đơn thuốc
    @Transactional
    public void deleteById(Integer id) {
        donThuocRepo.deleteById(id);
    }
}