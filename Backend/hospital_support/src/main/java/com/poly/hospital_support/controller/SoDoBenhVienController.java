package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.SoDoBenhVien;
import com.poly.hospital_support.service.SoDoBenhVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/so-do-benh-vien")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SoDoBenhVienController {

    private final SoDoBenhVienService soDoBenhVienService;

    @GetMapping
    public ResponseEntity<List<SoDoBenhVien>> getHospitalMaps() {
        return ResponseEntity.ok(soDoBenhVienService.getHospitalMaps());
    }
}
