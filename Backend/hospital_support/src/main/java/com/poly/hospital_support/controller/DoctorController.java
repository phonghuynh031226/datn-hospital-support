package com.poly.hospital_support.controller;

import com.poly.hospital_support.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin("*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/patients")
    public ResponseEntity<?> getPatients() {
        return ResponseEntity.ok(doctorService.getPatients());
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointments() {
        return ResponseEntity.ok(doctorService.getAppointments());
    }

    @GetMapping("/waiting-patients")
    public ResponseEntity<?> getWaitingPatients() {
        return ResponseEntity.ok(doctorService.getWaitingPatients());
    }
}
