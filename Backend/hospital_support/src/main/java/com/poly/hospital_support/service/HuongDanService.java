package com.poly.hospital_support.service;

import com.poly.hospital_support.entity.HuongDan;
import java.util.List;

public interface HuongDanService {
    List<HuongDan> getGuides(Integer categoryId, String search);
    HuongDan getGuideById(Integer id);
}
