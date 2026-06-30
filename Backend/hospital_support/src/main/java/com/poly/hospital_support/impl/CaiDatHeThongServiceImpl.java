package com.poly.hospital_support.impl;

import com.poly.hospital_support.dto.CaiDatHeThongRequest;
import com.poly.hospital_support.dto.CaiDatHeThongResponse;
import com.poly.hospital_support.entity.CaiDatHeThong;
import com.poly.hospital_support.repository.*;
import com.poly.hospital_support.service.CaiDatHeThongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CaiDatHeThongServiceImpl implements CaiDatHeThongService {

    @Autowired
    private CaiDatHeThongRepository caiDatHeThongRepository;

    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Autowired
    private LichHenRepository lichHenRepository;

    @Autowired
    private ThanhToanLichHenRepository thanhToanLichHenRepository;

    @Autowired
    private DonThuocRepository donThuocRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public CaiDatHeThongResponse getThongTinHeThong() {
        CaiDatHeThong config = caiDatHeThongRepository.findById(1).orElse(null);

        if (config == null) {
            CaiDatHeThongResponse response = new CaiDatHeThongResponse();

            response.setId(1);
            response.setTenBenhVien("Bệnh Viện Đa Khoa");
            response.setDuongDanLogo("");
            response.setDuongDanBanner("");
            response.setMauChuDao("#2563EB");
            response.setNgayCapNhat(LocalDateTime.now());

            return response;
        }

        return convertToResponse(config);
    }

    @Override
    @Transactional
    public CaiDatHeThongResponse capNhatHeThong(CaiDatHeThongRequest request) {
        CaiDatHeThong config = caiDatHeThongRepository.findById(1).orElse(null);

        if (config == null) {
            config = new CaiDatHeThong();
            config.setId(1);
        }

        config.setTenBenhVien(request.getTenBenhVien());
        config.setDuongDanLogo(request.getDuongDanLogo());
        config.setDuongDanBanner(request.getDuongDanBanner());
        config.setMauChuDao(request.getMauChuDao());
        config.setNgayCapNhat(LocalDateTime.now());

        CaiDatHeThong saved = caiDatHeThongRepository.save(config);

        return convertToResponse(saved);
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("tongSoBenhNhan", benhNhanRepository.count());
        metrics.put("tongSoLichHenHomNay", lichHenRepository.countByNgayHen(LocalDate.now()));
        metrics.put("tongSoNhanVien", nhanVienRepository.count());

        BigDecimal doanhThuKham = thanhToanLichHenRepository.sumSoTienByTrangThai("DA_THANH_TOAN");
        BigDecimal doanhThuThuoc = donThuocRepository.sumTongTienByTrangThai("DA_PHAT_THUOC");

        metrics.put("tongDoanhThuKham", doanhThuKham != null ? doanhThuKham : BigDecimal.ZERO);
        metrics.put("tongDoanhThuThuoc", doanhThuThuoc != null ? doanhThuThuoc : BigDecimal.ZERO);

        return metrics;
    }

    @Override
    public Map<String, BigDecimal> getBaoCaoDoanhThu(LocalDate fromDate, LocalDate toDate) {
        Map<String, BigDecimal> revenueMap = new HashMap<>();

        BigDecimal doanhThuKham = thanhToanLichHenRepository.tinhDoanhThuKhamTheoThoiGian(fromDate, toDate);
        BigDecimal doanhThuThuoc = donThuocRepository.tinhDoanhThuThuocTheoThoiGian(fromDate, toDate);

        BigDecimal sKham = doanhThuKham != null ? doanhThuKham : BigDecimal.ZERO;
        BigDecimal sThuoc = doanhThuThuoc != null ? doanhThuThuoc : BigDecimal.ZERO;

        revenueMap.put("doanhThuKham", sKham);
        revenueMap.put("doanhThuThuoc", sThuoc);
        revenueMap.put("tongDoanhThu", sKham.add(sThuoc));

        return revenueMap;
    }

    @Override
    @Transactional
    public void thayDoiTrangThaiNhanVien(Integer nhanVienId, Integer trangThai) {
        Integer maTaiKhoan = taiKhoanRepository.findIdByNhanVienId(nhanVienId);

        if (maTaiKhoan != null) {
            taiKhoanRepository.updateTrangThaiByTaiKhoanId(maTaiKhoan, trangThai);
        } else {
            throw new RuntimeException("Không tìm thấy tài khoản liên kết với nhân viên ID: " + nhanVienId);
        }
    }

    private CaiDatHeThongResponse convertToResponse(CaiDatHeThong config) {
        CaiDatHeThongResponse response = new CaiDatHeThongResponse();

        response.setId(config.getId());
        response.setTenBenhVien(config.getTenBenhVien());
        response.setDuongDanLogo(config.getDuongDanLogo());
        response.setDuongDanBanner(config.getDuongDanBanner());
        response.setMauChuDao(config.getMauChuDao());
        response.setNgayCapNhat(config.getNgayCapNhat());

        return response;
    }
}