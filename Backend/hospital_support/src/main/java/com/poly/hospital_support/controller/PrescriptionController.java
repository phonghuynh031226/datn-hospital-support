package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.Prescription;
import com.poly.hospital_support.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService service;

    @PutMapping("/{id}")
    public Prescription update(@PathVariable Long id,
                               @RequestBody Prescription prescription) {
        return service.update(id, prescription);
    }
}