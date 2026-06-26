package com.poly.hospital_support.service.impl;

import com.poly.hospital_support.entity.Medicine;
import com.poly.hospital_support.entity.MedicineBatch;
import com.poly.hospital_support.repository.MedicineBatchRepository;
import com.poly.hospital_support.repository.MedicineRepository;
import com.poly.hospital_support.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicineBatchRepository medicineBatchRepository;

    @Override
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @Override
    public Medicine createMedicine(Medicine medicine) {
        if (medicine.getInventoryQuantity() == null) {
            medicine.setInventoryQuantity(0);
        }
        return medicineRepository.save(medicine);
    }

    @Override
    @Transactional
    public MedicineBatch importMedicine(MedicineBatch medicineBatch) {
        if (medicineBatch.getMedicine() == null || medicineBatch.getMedicine().getId() == null) {
            throw new RuntimeException("Thuốc nhập kho không hợp lệ!");
        }

        // Tìm thuốc trong danh mục bảng 'thuoc'
        Medicine medicine = medicineRepository.findById(medicineBatch.getMedicine().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại thuốc này trong danh mục!"));

        // Thực hiện cộng dồn số lượng tồn kho
        int soLuongHienTai = medicine.getInventoryQuantity() != null ? medicine.getInventoryQuantity() : 0;
        int soLuongNhapMoi = medicineBatch.getQuantity();
        medicine.setInventoryQuantity(soLuongHienTai + soLuongNhapMoi);

        // Lưu lại số lượng mới vào bảng 'thuoc'
        medicineRepository.save(medicine);

        // Thiết lập thông tin và lưu vào bảng 'nhap_kho_thuoc'
        medicineBatch.setMedicine(medicine);
        if (medicineBatch.getImportDate() == null) {
            medicineBatch.setImportDate(LocalDateTime.now());
        }

        return medicineBatchRepository.save(medicineBatch);
    }
}