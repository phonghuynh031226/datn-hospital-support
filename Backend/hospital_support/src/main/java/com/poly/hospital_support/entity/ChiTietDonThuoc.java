package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_don_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDonThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_thuoc", nullable = false)
    private DonThuoc donThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thuoc", nullable = false)
    private Thuoc thuoc;

    /** Số lượng bác sĩ yêu cầu (dùng để Hold) */
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    /** Số lượng dược sĩ thực tế phát (điền khi chuẩn bị) */
    @Column(name = "so_luong_thuc_phat")
    private Integer soLuongThucPhat;

    @Column(name = "huong_dan_su_dung", length = 255)
    private String huongDanSuDung;

    /**
     * 'TRONG_DON' | 'NGOAI_DON'
     */
    @Column(name = "loai", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'TRONG_DON'")
    private String loai;
}
