package com.poly.hospital_support.controller;



import com.poly.hospital_support.entity.CTDonThuoc;
import com.poly.hospital_support.service.CTDonThuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ct-don-thuoc")
public class CTDonThuocController {

    @Autowired
    private CTDonThuocService ctDonThuocService;

    // Lấy tất cả chi tiết thuốc của 1 đơn thuốc
    @GetMapping("/don-thuoc/{donThuocId}")
    public List<CTDonThuoc> getByDonThuocId(@PathVariable Integer donThuocId) {
        return ctDonThuocService.findByDonThuocId(donThuocId);
    }

    // Thêm hoặc sửa chi tiết thuốc
    @PostMapping("/luu")
    public CTDonThuoc save(@RequestBody CTDonThuoc ctDonThuoc) {
        return ctDonThuocService.save(ctDonThuoc);
    }

    // Xóa chi tiết thuốc
    @DeleteMapping("/xoa/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ctDonThuocService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
