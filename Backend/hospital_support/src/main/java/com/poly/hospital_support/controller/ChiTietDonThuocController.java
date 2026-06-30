package com.poly.hospital_support.controller;

import com.poly.hospital_support.service.ChiTietDonThuocService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChiTietDonThuocController {

    private final ChiTietDonThuocService Service;
}
