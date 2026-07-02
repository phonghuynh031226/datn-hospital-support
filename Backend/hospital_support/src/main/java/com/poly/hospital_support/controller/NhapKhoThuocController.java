package com.poly.hospital_support.controller;

import com.poly.hospital_support.service.NhapKhoThuocService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NhapKhoThuocController {

    private final NhapKhoThuocService service;

}
