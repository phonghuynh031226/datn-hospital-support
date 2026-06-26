package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.Medicine;
import com.poly.hospital_support.entity.MedicineBatch;
import java.util.List;

public interface WarehouseService {
    List<Medicine> getAllMedicines();
    Medicine createMedicine(Medicine medicine);
    MedicineBatch importMedicine(MedicineBatch medicineBatch); // Sửa từ InventoryLog sang MedicineBatch
}