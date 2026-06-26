package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.ViTriPhong;
import com.poly.hospital_support.service.ViTriPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vi-tri-phong")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ViTriPhongController {

    private final ViTriPhongService viTriPhongService;

    @GetMapping
    public ResponseEntity<List<ViTriPhong>> getRooms() {
        return ResponseEntity.ok(viTriPhongService.getRooms());
    }
}
