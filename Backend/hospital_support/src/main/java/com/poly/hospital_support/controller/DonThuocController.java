package com.poly.hospital_support.controller;

import com.poly.hospital_support.dto.ChiTietDonThuocResponse;
import com.poly.hospital_support.dto.DonThuocResponse;
import com.poly.hospital_support.entity.ChiTietDonThuoc;
import com.poly.hospital_support.entity.DonThuoc;
import com.poly.hospital_support.repository.ChiTietDonThuocRepository;
import com.poly.hospital_support.service.DonThuocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/don-thuoc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonThuocController {

    private final DonThuocService donThuocService;
    private final ChiTietDonThuocRepository chiTietDonThuocRepository;

    private DonThuocResponse mapToResponse(DonThuoc dt) {
        List<ChiTietDonThuoc> details = chiTietDonThuocRepository.findByDonThuocId(dt.getId());
        List<ChiTietDonThuocResponse> detailResponses = details.stream().map(d -> ChiTietDonThuocResponse.builder()
                .id(d.getId())
                .thuocId(d.getThuoc().getId())
                .tenThuoc(d.getThuoc().getTenThuoc())
                .donViTinh(d.getThuoc().getDonViTinh())
                .donGia(d.getThuoc().getDonGia())
                .soLuong(d.getSoLuong())
                .soLuongThucPhat(d.getSoLuongThucPhat())
                .huongDanSuDung(d.getHuongDanSuDung())
                .loai(d.getLoai())
                .build()).collect(Collectors.toList());

        return DonThuocResponse.builder()
                .id(dt.getId())
                .benhAnId(dt.getBenhAn().getId())
                .bacSiName(dt.getBacSi().getHoTen())
                .benhNhanName(dt.getBenhNhan().getHoTen())
                .trangThai(dt.getTrangThai())
                .tongTien(dt.getTongTien())
                .ngayTao(dt.getNgayTao())
                .details(detailResponses)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPrescriptionById(@PathVariable Integer id) {
        try {
            DonThuoc dt = donThuocService.getPrescriptionById(id);
            return ResponseEntity.ok(mapToResponse(dt));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/benh-an/{benhAnId}")
    public ResponseEntity<?> getPrescriptionByBenhAnId(@PathVariable Integer benhAnId) {
        try {
            DonThuoc dt = donThuocService.getPrescriptionByBenhAnId(benhAnId);
            return ResponseEntity.ok(mapToResponse(dt));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(@PathVariable Integer id) {
        byte[] pdfBytes = donThuocService.generatePrescriptionPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "don_thuoc_" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DonThuocResponse>> getPrescriptionsByPatientId(@PathVariable Integer patientId) {
        List<DonThuoc> list = donThuocService.getPrescriptionsByPatientId(patientId);
        List<DonThuocResponse> responses = list.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
