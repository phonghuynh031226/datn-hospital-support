package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.Khoa;
import java.util.List;

public interface KhoaService {
    List<Khoa> getDepartments(String search);
    Khoa getDepartmentById(Integer id);
    Khoa getDepartmentBySlug(String slug);
}
