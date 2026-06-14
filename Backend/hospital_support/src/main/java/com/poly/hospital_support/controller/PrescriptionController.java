package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.Prescription;
import com.poly.hospital_support.service.PrescriptionService;
import org.springframework.web.bind.annotation.*;


@PutMapping("/{id}")
public Prescription update(@PathVariable Integer id,
                           @RequestBody Prescription prescription) {
    return service.update(id, prescription);
}