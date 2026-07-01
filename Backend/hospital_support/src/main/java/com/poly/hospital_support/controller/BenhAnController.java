package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.BenhAn;
import com.poly.hospital_support.service.BenhAnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/benh-an")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BenhAnController {

    private final BenhAnService benhAnService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BenhAn>> getPatientMedicalHistory(
            @PathVariable Integer patientId,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(benhAnService.getPatientMedicalHistory(patientId, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable Integer id) {
        try {
            BenhAn ba = benhAnService.getMedicalRecordById(id);
            return ResponseEntity.ok(ba);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
