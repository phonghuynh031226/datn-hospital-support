package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tin_tuc")
@Data
public class TinTuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "tom_tat", length = 500)
    private String tomTat;

    @Column(name = "noi_dung", columnDefinition = "LONGTEXT")
    private String noiDung;

    @Column(name = "hinh_anh", length = 1000)
    private String hinhAnh;

    @Column(name = "tac_gia", length = 100)
    private String tacGia;

    @Column(name = "danh_muc", length = 100)
    private String danhMuc;

    @Column(name = "da_ghim")
    private Boolean daGhim = false;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "slug", length = 320, unique = true)
    private String slug;
}