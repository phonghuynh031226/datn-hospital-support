package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.DanhMucTinTuc;
import com.poly.hospital_support.repository.DanhMucTinTucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/danh-muc-tin-tuc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DanhMucTinTucController {

    private final DanhMucTinTucRepository repository;

    @GetMapping
    public ResponseEntity<List<DanhMucTinTuc>> getAllCategories() {
        return ResponseEntity.ok(repository.findAll());
    }
}
