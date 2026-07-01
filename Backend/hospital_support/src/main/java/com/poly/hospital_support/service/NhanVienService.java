package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.NhanVien;
import java.util.List;

public interface NhanVienService {
    List<NhanVien> getAllDoctors(Integer departmentId, String search);
    NhanVien getDoctorById(Integer id);
}
