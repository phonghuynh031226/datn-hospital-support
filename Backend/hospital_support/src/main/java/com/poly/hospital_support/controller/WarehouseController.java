package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.Medicine;
import com.poly.hospital_support.entity.MedicineBatch;
import com.poly.hospital_support.service.WarehouseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@CrossOrigin(origins = "*")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/medicines")
    public List<Medicine> getAllMedicines() {
        return warehouseService.getAllMedicines();
    }

    @PostMapping("/medicines")
    public ResponseEntity<Medicine> createMedicine(
            @RequestBody Medicine medicine) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(warehouseService.createMedicine(medicine));
    }

    @PostMapping("/import")
    public ResponseEntity<MedicineBatch> importMedicine(
            @RequestBody MedicineBatch medicineBatch) {

        return ResponseEntity.ok(
                warehouseService.importMedicine(medicineBatch));
    }

    @GetMapping("/import-history")
    public List<MedicineBatch> getImportHistory() {
        return warehouseService.getAllImportHistory();
    }
}