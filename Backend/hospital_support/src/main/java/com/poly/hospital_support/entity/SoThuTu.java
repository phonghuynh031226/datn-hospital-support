package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "so_thu_tu",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_stt_bacsi_ngay",
        columnNames = {"ma_bac_si", "ngay_kham", "so_thu_tu"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoThuTu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Bắt buộc có lịch hẹn (online hoặc tại quầy) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lich_hen", nullable = false)
    private LichHen lichHen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private BenhNhan benhNhan;

    /** Xác định "phòng" bác sĩ trong ca */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private NhanVien bacSi;

    @Column(name = "ngay_kham", nullable = false)
    private LocalDate ngayKham;

    /** STT tăng dần theo từng bác sĩ mỗi ngày */
    @Column(name = "so_thu_tu", nullable = false)
    private Integer soThuTu;

    /**
     * 'KHAM_MOI' | 'KHAM_LAI'
     */
    @Column(name = "lan_kham", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'KHAM_MOI'")
    private String lanKham;

    /**
     * 'CHO_GOI' | 'DANG_KHAM' | 'HOAN_THANH'
     */
    @Column(name = "trang_thai", nullable = false, length = 30,
            columnDefinition = "VARCHAR(30) DEFAULT 'CHO_GOI'")
    private String trangThai;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
