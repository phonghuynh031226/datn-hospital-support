package com.poly.hospital_support.controller;

import com.poly.hospital_support.dto.CaiDatHeThongRequest;
import com.poly.hospital_support.dto.CaiDatHeThongResponse;
import com.poly.hospital_support.service.CaiDatHeThongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/giam-doc")
public class CaiDatHeThongController {

    @Autowired
    private CaiDatHeThongService caiDatHeThongService;

    @GetMapping("/cai-dat")
    public ResponseEntity<CaiDatHeThongResponse> getSystemConfig() {
        return ResponseEntity.ok(caiDatHeThongService.getThongTinHeThong());
    }

    @PutMapping("/cai-dat")
    public ResponseEntity<CaiDatHeThongResponse> updateSystemConfig(
            @Valid @RequestBody CaiDatHeThongRequest request
    ) {
        return ResponseEntity.ok(caiDatHeThongService.capNhatHeThong(request));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        return ResponseEntity.ok(caiDatHeThongService.getDashboardData());
    }

    @GetMapping("/bao-cao/doanh-thu")
    public ResponseEntity<Map<String, BigDecimal>> getBaoCaoDoanhThu(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(caiDatHeThongService.getBaoCaoDoanhThu(fromDate, toDate));
    }

    @PutMapping("/nhan-vien/{id}/trang-thai")
    public ResponseEntity<String> thayDoiTrangThaiNhanVien(
            @PathVariable Integer id,
            @RequestParam Integer trangThai
    ) {
        caiDatHeThongService.thayDoiTrangThaiNhanVien(id, trangThai);
        return ResponseEntity.ok("Cập nhật trạng thái tài khoản nhân viên thành công!");
    }
}