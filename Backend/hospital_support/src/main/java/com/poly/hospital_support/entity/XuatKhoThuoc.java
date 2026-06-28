package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "xuat_kho_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuatKhoThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Lô thuốc bị xuất */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lo_thuoc", nullable = false)
    private NhapKhoThuoc loThuoc;

    /** Nhân viên kho thực hiện */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "so_luong_xuat", nullable = false)
    private Integer soLuongXuat;

    /**
     * 'HET_HAN' | 'HU_HONG' | 'KIEM_KE'
     */
    @Column(name = "ly_do", nullable = false, length = 30,
            columnDefinition = "VARCHAR(30) DEFAULT 'HET_HAN'")
    private String lyDo;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ngay_xuat", insertable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayXuat;
}
