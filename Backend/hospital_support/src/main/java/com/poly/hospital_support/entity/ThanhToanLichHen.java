package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan_lich_hen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanhToanLichHen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lich_hen", nullable = false, unique = true)
    private LichHen lichHen;

    @Column(name = "so_tien", nullable = false, columnDefinition = "DECIMAL(12,2) DEFAULT 0")
    private BigDecimal soTien;

    /**
     * 'ONLINE' | 'TAI_QUAY'
     */
    @Column(name = "phuong_thuc", nullable = false, length = 30,
            columnDefinition = "VARCHAR(30) DEFAULT 'TAI_QUAY'")
    private String phuongThuc;

    /**
     * 'CHO_THANH_TOAN' | 'DA_THANH_TOAN'
     */
    @Column(name = "trang_thai", nullable = false, length = 30,
            columnDefinition = "VARCHAR(30) DEFAULT 'CHO_THANH_TOAN'")
    private String trangThai;

    /** Điều dưỡng thu tiền tại quầy (NULL nếu thanh toán online) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien_thu")
    private NhanVien nhanVienThu;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
