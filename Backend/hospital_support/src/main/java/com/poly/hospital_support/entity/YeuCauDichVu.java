package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "yeu_cau_dich_vu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YeuCauDichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_an", nullable = false)
    private BenhAn benhAn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private NhanVien bacSi;

    @Column(name = "ten_dich_vu", nullable = false, length = 200)
    private String tenDichVu;

    @Column(name = "ket_qua", columnDefinition = "TEXT")
    private String ketQua;

    /**
     * 'CHO_THUC_HIEN' | 'DA_CO_KET_QUA'
     */
    @Column(name = "trang_thai", length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'CHO_THUC_HIEN'")
    private String trangThai;
}
