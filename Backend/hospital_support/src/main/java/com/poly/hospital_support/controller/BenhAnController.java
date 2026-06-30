package com.poly.hospital_support.controller;



import com.poly.hospital_support.entity.BenhAn;
import com.poly.hospital_support.service.BenhAnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benh-an")
public class BenhAnController {

    @Autowired
    private BenhAnService benhAnService;

    // Lấy tất cả bệnh án
    @GetMapping("/tat-ca")
    public List<BenhAn> getAll() {
        return benhAnService.findAll();
    }

    // Lấy bệnh án theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BenhAn> getById(@PathVariable Integer id) {
        return benhAnService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo bệnh án mới
    @PostMapping("/tao-moi")
    public BenhAn create(@RequestBody BenhAn benhAn) {
        return benhAnService.save(benhAn);
    }

    // Xóa bệnh án
    @DeleteMapping("/xoa/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        benhAnService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}