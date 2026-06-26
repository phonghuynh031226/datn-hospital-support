package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "benh_an")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenhAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lich_hen", unique = true)
    private LichHen lichHen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private NhanVien bacSi;

    @Column(name = "trieu_chung", columnDefinition = "TEXT")
    private String trieuChung;

    @Column(name = "chan_doan", columnDefinition = "TEXT")
    private String chanDoan;

    @Column(name = "ket_luan", columnDefinition = "TEXT")
    private String ketLuan;

    /**
     * 'CHO_KHAM' | 'DANG_KHAM' | 'CHO_XET_NGHIEM' | 'CHO_KE_DON' | 'HOAN_THANH'
     */
    @Column(name = "trang_thai", length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'CHO_KHAM'")
    private String trangThai;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
