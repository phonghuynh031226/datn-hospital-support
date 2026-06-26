package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.Khoa;
import com.poly.hospital_support.entity.BacSiKhoa;
import com.poly.hospital_support.service.KhoaService;
import com.poly.hospital_support.repository.BacSiKhoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/khoa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhoaController {

    private final KhoaService khoaService;
    private final BacSiKhoaRepository bacSiKhoaRepository;

    @GetMapping
    public ResponseEntity<?> getDepartments(@RequestParam(required = false) String search) {
        List<Khoa> depts = khoaService.getDepartments(search);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Khoa k : depts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", k.getId());
            map.put("tenKhoa", k.getTenKhoa());
            map.put("slug", k.getSlug());
            map.put("moTaNgan", k.getMoTaNgan());
            map.put("noiDungGioiThieu", k.getNoiDungGioiThieu());
            map.put("trangThietBi", k.getTrangThietBi());
            map.put("thuTu", k.getThuTu());
            map.put("trangThai", k.getTrangThai());
            
            // Find Trưởng khoa
            List<BacSiKhoa> truongKhoaList = bacSiKhoaRepository.findTruongKhoaByKhoaId(k.getId());
            if (truongKhoaList != null && !truongKhoaList.isEmpty()) {
                map.put("truongKhoa", truongKhoaList.get(0).getNhanVien().getHoTen());
            } else {
                map.put("truongKhoa", "Đang cập nhật");
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Integer id) {
        try {
            Khoa k = khoaService.getDepartmentById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("id", k.getId());
            map.put("tenKhoa", k.getTenKhoa());
            map.put("slug", k.getSlug());
            map.put("moTaNgan", k.getMoTaNgan());
            map.put("noiDungGioiThieu", k.getNoiDungGioiThieu());
            map.put("trangThietBi", k.getTrangThietBi());
            map.put("thuTu", k.getThuTu());
            map.put("trangThai", k.getTrangThai());
            
            List<BacSiKhoa> truongKhoaList = bacSiKhoaRepository.findTruongKhoaByKhoaId(k.getId());
            if (truongKhoaList != null && !truongKhoaList.isEmpty()) {
                map.put("truongKhoa", truongKhoaList.get(0).getNhanVien().getHoTen());
            } else {
                map.put("truongKhoa", "Đang cập nhật");
            }
            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getDepartmentBySlug(@PathVariable String slug) {
        try {
            Khoa k = khoaService.getDepartmentBySlug(slug);
            Map<String, Object> map = new HashMap<>();
            map.put("id", k.getId());
            map.put("tenKhoa", k.getTenKhoa());
            map.put("slug", k.getSlug());
            map.put("moTaNgan", k.getMoTaNgan());
            map.put("noiDungGioiThieu", k.getNoiDungGioiThieu());
            map.put("trangThietBi", k.getTrangThietBi());
            map.put("thuTu", k.getThuTu());
            map.put("trangThai", k.getTrangThai());
            
            List<BacSiKhoa> truongKhoaList = bacSiKhoaRepository.findTruongKhoaByKhoaId(k.getId());
            if (truongKhoaList != null && !truongKhoaList.isEmpty()) {
                map.put("truongKhoa", truongKhoaList.get(0).getNhanVien().getHoTen());
            } else {
                map.put("truongKhoa", "Đang cập nhật");
            }
            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
