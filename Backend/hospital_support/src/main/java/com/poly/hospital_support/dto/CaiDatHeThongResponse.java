package com.poly.hospital_support.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaiDatHeThongResponse {

    private Integer id;

    private String tenBenhVien;

    private String duongDanLogo;

    private String duongDanBanner;

    private String mauChuDao;

    private LocalDateTime ngayCapNhat;

}