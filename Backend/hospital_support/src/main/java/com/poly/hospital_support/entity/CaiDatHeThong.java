package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "cai_dat_he_thong")
@Data
public class CaiDatHeThong {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten_benh_vien", nullable = false)
    private String tenBenhVien;

    @Column(name = "duong_dan_logo")
    private String duongDanLogo;

    @Column(name = "duong_dan_banner")
    private String duongDanBanner;

    @Column(name = "mau_chu_dao")
    private String mauChuDao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}