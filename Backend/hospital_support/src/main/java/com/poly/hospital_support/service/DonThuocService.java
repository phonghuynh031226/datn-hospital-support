package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.DonThuoc;
import java.util.List;

public interface DonThuocService {
    DonThuoc getPrescriptionByBenhAnId(Integer benhAnId);
    DonThuoc getPrescriptionById(Integer id);
    byte[] generatePrescriptionPdf(Integer id);
    List<DonThuoc> getPrescriptionsByPatientId(Integer patientId);
}
