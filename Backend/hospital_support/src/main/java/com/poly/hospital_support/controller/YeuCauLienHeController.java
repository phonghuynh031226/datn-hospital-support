package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.YeuCauLienHe;
import com.poly.hospital_support.service.YeuCauLienHeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lien-he")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class YeuCauLienHeController {

    private final YeuCauLienHeService yeuCauLienHeService;

    @PostMapping
    public ResponseEntity<?> submitInquiry(@Valid @RequestBody YeuCauLienHe inquiry) {
        try {
            YeuCauLienHe saved = yeuCauLienHeService.submitInquiry(inquiry);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
