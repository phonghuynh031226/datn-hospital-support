package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.BenhAn;
import java.util.List;

public interface BenhAnService {
    List<BenhAn> getPatientMedicalHistory(Integer patientId, String search);
    BenhAn getMedicalRecordById(Integer id);
}
