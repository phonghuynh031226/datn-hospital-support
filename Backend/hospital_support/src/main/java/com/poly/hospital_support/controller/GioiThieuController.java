package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.GioiThieu;
import com.poly.hospital_support.service.GioiThieuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gioi-thieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GioiThieuController {

    private final GioiThieuService gioiThieuService;

    @GetMapping
    public ResponseEntity<GioiThieu> getIntroduction() {
        return ResponseEntity.ok(gioiThieuService.getIntroduction());
    }
}
