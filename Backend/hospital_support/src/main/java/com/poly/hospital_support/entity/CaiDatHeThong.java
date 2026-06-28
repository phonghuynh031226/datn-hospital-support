package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Singleton table - luôn có ID = 1
 * Quản lý cài đặt hiển thị toàn site: logo, banner, màu chủ đạo
 */
@Entity
@Table(name = "cai_dat_he_thong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaiDatHeThong {

    @Id
    @Column(name = "id", columnDefinition = "INT DEFAULT 1")
    private Integer id;

    @Column(name = "ten_benh_vien", length = 200)
    private String tenBenhVien;

    /** Logo ở header (góc trên trái) */
    @Column(name = "duong_dan_logo", length = 500)
    private String duongDanLogo;

    /** Ảnh banner trang chủ (hero section) */
    @Column(name = "duong_dan_banner", length = 500)
    private String duongDanBanner;

    /** Mã màu theme (VD: '#0d6efd') */
    @Column(name = "mau_chu_dao", length = 20)
    private String mauChuDao;

    @Column(name = "ngay_cap_nhat", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime ngayCapNhat;
}
