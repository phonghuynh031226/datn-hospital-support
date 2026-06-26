package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.Medicine;
import com.poly.hospital_support.entity.MedicineBatch; // Đã đổi từ InventoryLog sang MedicineBatch
import com.poly.hospital_support.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        List<Medicine> medicines = warehouseService.getAllMedicines();
        return ResponseEntity.ok(medicines);
    }

    @PostMapping("/medicines")
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        Medicine newMedicine = warehouseService.createMedicine(medicine);
        return ResponseEntity.ok(newMedicine);
    }

    @PostMapping("/import")
    public ResponseEntity<MedicineBatch> importMedicine(@RequestBody MedicineBatch medicineBatch) {
        MedicineBatch result = warehouseService.importMedicine(medicineBatch);
        return ResponseEntity.ok(result);
    }
}