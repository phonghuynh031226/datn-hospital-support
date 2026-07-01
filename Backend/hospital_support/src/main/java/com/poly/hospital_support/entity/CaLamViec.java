package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "ca_lam_viec",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_bacsi_ngay_ca",
        columnNames = {"ma_bac_si", "ngay_lam", "ca_lam"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaLamViec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private NhanVien bacSi;

    @Column(name = "ngay_lam", nullable = false)
    private LocalDate ngayLam;

    /**
     * 'SANG' | 'CHIEU' | 'CA_NGAY'
     */
    @Column(name = "ca_lam", nullable = false, length = 20)
    private String caLam;

    @Column(name = "phong_kham", nullable = false, length = 50)
    private String phongKham;

    /**
     * 'CHO_BAT_DAU' | 'DANG_KHAM' | 'KET_THUC'
     */
    @Column(name = "trang_thai", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'CHO_BAT_DAU'")
    private String trangThai;
}
