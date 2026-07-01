package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "khoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Khoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_khoa", nullable = false, length = 150)
    private String tenKhoa;

    @Column(name = "slug", unique = true, nullable = false, length = 170)
    private String slug;

    @Column(name = "mo_ta_ngan", length = 300)
    private String moTaNgan;

    @Column(name = "noi_dung_gioi_thieu", columnDefinition = "LONGTEXT")
    private String noiDungGioiThieu;

    @Column(name = "trang_thiet_bi", columnDefinition = "LONGTEXT")
    private String trangThietBi;

    @Column(name = "thu_tu", columnDefinition = "INT DEFAULT 0")
    private Integer thuTu;

    @Column(name = "trang_thai", columnDefinition = "TINYINT DEFAULT 1")
    private Byte trangThai;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
