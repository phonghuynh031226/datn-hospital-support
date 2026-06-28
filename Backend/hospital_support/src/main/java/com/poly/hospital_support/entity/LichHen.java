package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_hen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichHen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private NhanVien bacSi;

    @Column(name = "ngay_hen", nullable = false)
    private LocalDate ngayHen;

    @Column(name = "khung_gio", length = 50)
    private String khungGio;

    @Column(name = "trieu_chung", columnDefinition = "TEXT")
    private String trieuChung;

    /**
     * 'CHO_XAC_NHAN' | 'DA_XAC_NHAN' | 'DA_HUY'
     */
    @Column(name = "trang_thai", length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'CHO_XAC_NHAN'")
    private String trangThai;

    /**
     * 'ONLINE' | 'TAI_QUAY'
     */
    @Column(name = "nguon_tao", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ONLINE'")
    private String nguonTao;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
