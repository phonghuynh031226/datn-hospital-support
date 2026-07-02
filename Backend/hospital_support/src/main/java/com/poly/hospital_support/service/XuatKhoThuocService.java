package com.poly.hospital_support.service;



import com.poly.hospital_support.entity.XuatKhoThuoc;

import java.util.List;
import java.util.Optional;

public interface XuatKhoThuocService {

    // Lấy danh sách phiếu xuất
    List<XuatKhoThuoc> findAll();

    // Tìm theo id
    Optional<XuatKhoThuoc> findById(Integer id);

    // Thêm phiếu xuất
    XuatKhoThuoc save(XuatKhoThuoc xuatKhoThuoc);

    // Cập nhật phiếu xuất
    XuatKhoThuoc update(XuatKhoThuoc xuatKhoThuoc);

    // Xóa phiếu xuất
    void deleteById(Integer id);

    // Tìm theo mã phiếu (nếu có)
    Optional<XuatKhoThuoc> findByMaPhieu(String maPhieu);
}