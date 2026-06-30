package com.poly.hospital_support.controller;


import com.poly.hospital_support.entity.DonThuoc;
import com.poly.hospital_support.service.DonThuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/don-thuoc")
public class DonThuocController {

    @Autowired
    private DonThuocService donThuocService;

    // 1. Tạo mới đơn thuốc
    @PostMapping("/tao-moi")
    public ResponseEntity<DonThuoc> taoDonThuoc(@RequestBody DonThuoc donThuoc) {
        return ResponseEntity.ok(donThuocService.saveDonThuoc(donThuoc));
    }

    // 2. Tìm đơn thuốc theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DonThuoc> getDonThuoc(@PathVariable Integer id) {
        return donThuocService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Tìm đơn thuốc theo mã bệnh án
    @GetMapping("/benh-an/{benhAnId}")
    public ResponseEntity<DonThuoc> getByBenhAnId(@PathVariable Integer benhAnId) {
        DonThuoc donThuoc = donThuocService.findByBenhAnId(benhAnId);
        return (donThuoc != null) ? ResponseEntity.ok(donThuoc) : ResponseEntity.notFound().build();
    }

    // 4. Lấy tất cả đơn thuốc
    @GetMapping("/tat-ca")
    public ResponseEntity<List<DonThuoc>> getAllDonThuoc() {
        return ResponseEntity.ok(donThuocService.findAll());
    }

    // 5. Xóa đơn thuốc
    @DeleteMapping("/xoa/{id}")
    public ResponseEntity<Void> deleteDonThuoc(@PathVariable Integer id) {
        donThuocService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
