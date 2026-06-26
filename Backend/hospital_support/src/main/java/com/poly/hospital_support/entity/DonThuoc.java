package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_an", nullable = false, unique = true)
    private BenhAn benhAn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private NhanVien bacSi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    /**
     * 'CHO_PHAT_THUOC' | 'DANG_CHUAN_BI' | 'CHO_THANH_TOAN'
     * | 'YEU_CAU_SUA_DON' | 'DA_PHAT_THUOC'
     */
    @Column(name = "trang_thai", nullable = false, length = 30,
            columnDefinition = "VARCHAR(30) DEFAULT 'CHO_PHAT_THUOC'")
    private String trangThai;

    @Column(name = "tong_tien", columnDefinition = "DECIMAL(12,2) DEFAULT 0")
    private BigDecimal tongTien;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
