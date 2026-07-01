package com.poly.hospital_support.controller;

import com.poly.hospital_support.dto.LichHenRequest;
import com.poly.hospital_support.dto.LichHenResponse;
import com.poly.hospital_support.service.LichHenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lich-hen")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LichHenController {

    private final LichHenService service;

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody LichHenRequest request) {
        try {
            LichHenResponse response = service.createAppointment(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LichHenResponse>> getAppointmentsByPatientId(@PathVariable Integer patientId) {
        return ResponseEntity.ok(service.getAppointmentsByPatientId(patientId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Integer id) {
        try {
            LichHenResponse response = service.cancelAppointment(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> confirmPayment(@PathVariable Integer id) {
        try {
            LichHenResponse response = service.confirmPayment(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
