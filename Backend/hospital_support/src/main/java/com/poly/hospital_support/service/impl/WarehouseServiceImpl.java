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
        if (medicine.getSoLuongTon() == null) {
            medicine.setSoLuongTon(0);
        }
        return medicineRepository.save(medicine);
    }

    @Override
    @Transactional
    public MedicineBatch importMedicine(MedicineBatch medicineBatch) {

        if (medicineBatch.getMedicine() == null
                || medicineBatch.getMedicine().getId() == null) {
            throw new RuntimeException("Thuốc không hợp lệ!");
        }

        Medicine medicine = medicineRepository.findById(
                        medicineBatch.getMedicine().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc"));

        Integer tonKho = medicine.getSoLuongTon() == null ? 0 : medicine.getSoLuongTon();

        medicine.setSoLuongTon(
                tonKho + medicineBatch.getSoLuongNhap());

        medicineRepository.save(medicine);

        medicineBatch.setSoLuongConLai(
                medicineBatch.getSoLuongNhap());

        return medicineBatchRepository.save(medicineBatch);
    }
    @Override
    public List<MedicineBatch> getAllImportHistory() {
        return medicineBatchRepository.findAll();
    }
}