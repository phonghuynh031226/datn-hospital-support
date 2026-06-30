package com.poly.hospital_support.controller;

import com.poly.hospital_support.dto.TinTucRequest;
import com.poly.hospital_support.dto.TinTucResponse;
import com.poly.hospital_support.service.TinTucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/giam-doc/tin-tuc")
public class TinTucController {

    @Autowired
    private TinTucService tinTucService;

    @GetMapping
    public ResponseEntity<List<TinTucResponse>> getAllTinTuc() {
        return ResponseEntity.ok(tinTucService.getAllTinTuc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TinTucResponse> getTinTucById(@PathVariable Integer id) {
        return ResponseEntity.ok(tinTucService.getTinTucById(id));
    }

    @GetMapping("/ghim")
    public ResponseEntity<List<TinTucResponse>> getTinDaGhim() {
        return ResponseEntity.ok(tinTucService.getTinDaGhim());
    }

    @PostMapping
    public ResponseEntity<TinTucResponse> createTinTuc(@RequestBody TinTucRequest request) {
        return ResponseEntity.ok(tinTucService.createTinTuc(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TinTucResponse> updateTinTuc(
            @PathVariable Integer id,
            @RequestBody TinTucRequest request
    ) {
        return ResponseEntity.ok(tinTucService.updateTinTuc(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTinTuc(@PathVariable Integer id) {
        tinTucService.deleteTinTuc(id);
        return ResponseEntity.ok("Xóa tin tức thành công!");
    }

    @PutMapping("/{id}/ghim")
    public ResponseEntity<TinTucResponse> ghimTinTuc(@PathVariable Integer id) {
        return ResponseEntity.ok(tinTucService.ghimTinTuc(id));
    }
}