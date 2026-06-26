package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.HuongDan;
import com.poly.hospital_support.service.HuongDanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/huong-dan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HuongDanController {

    private final HuongDanService huongDanService;

    @GetMapping
    public ResponseEntity<List<HuongDan>> getGuides(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(huongDanService.getGuides(categoryId, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGuideById(@PathVariable Integer id) {
        try {
            HuongDan hd = huongDanService.getGuideById(id);
            return ResponseEntity.ok(hd);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
