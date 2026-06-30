package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.GioiThieu;
import com.poly.hospital_support.repository.GioiThieuRepository;
import com.poly.hospital_support.service.GioiThieuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GioiThieuServiceImpl implements GioiThieuService {

    private final GioiThieuRepository gioiThieuRepository;

    @Override
    public GioiThieu getIntroduction() {
        return gioiThieuRepository.findById(1)
                .orElseGet(() -> {
                    GioiThieu gt = GioiThieu.builder()
                            .id(1)
                            .tieuDe("Giới thiệu về Bệnh viện Đa khoa")
                            .noiDung("Hệ thống quản lý bệnh viện và hỗ trợ đặt lịch khám trực tuyến hàng đầu.")
                            .build();
                    return gioiThieuRepository.save(gt);
                });
    }
}
