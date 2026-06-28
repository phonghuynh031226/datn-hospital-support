package com.poly.hospital_support.service;

import com.poly.hospital_support.dto.CaiDatHeThongRequest;
import com.poly.hospital_support.dto.CaiDatHeThongResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface CaiDatHeThongService {

    CaiDatHeThongResponse getThongTinHeThong();

    CaiDatHeThongResponse capNhatHeThong(CaiDatHeThongRequest request);

    Map<String, Object> getDashboardData();

    Map<String, BigDecimal> getBaoCaoDoanhThu(LocalDate fromDate, LocalDate toDate);

    void thayDoiTrangThaiNhanVien(Integer nhanVienId, Integer trangThai);
}