package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhap_kho_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhapKhoThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thuoc", nullable = false)
    private Thuoc thuoc;

    /** Nhân viên kho thực hiện nhập */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien", nullable = false)
    private NhanVien nhanVien;

    /** Số lô sản xuất (VD: LOT-2024-001) */
    @Column(name = "so_lo", nullable = false, length = 50)
    private String soLo;

    /** Số lượng nhập ban đầu */
    @Column(name = "so_luong_nhap", nullable = false)
    private Integer soLuongNhap;

    /** Còn lại trong lô (giảm khi phát thuốc) */
    @Column(name = "so_luong_con_lai", nullable = false)
    private Integer soLuongConLai;

    @Column(name = "don_gia_nhap", columnDefinition = "DECIMAL(12,2)")
    private BigDecimal donGiaNhap;

    /** Hạn sử dụng của lô này */
    @Column(name = "han_su_dung", nullable = false)
    private LocalDate hanSuDung;

    @Column(name = "ngay_nhap", insertable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayNhap;

    /**
     * 'HOAT_DONG' | 'HET_HANG' | 'HET_HAN' | 'DA_XUAT'
     */
    @Column(name = "trang_thai", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'HOAT_DONG'")
    private String trangThai;
}
