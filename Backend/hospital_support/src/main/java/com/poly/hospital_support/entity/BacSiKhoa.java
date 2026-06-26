package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "bac_si_khoa",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_bacsi_khoa",
        columnNames = {"ma_nhan_vien", "ma_khoa"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BacSiKhoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khoa", nullable = false)
    private Khoa khoa;

    /** VD: Trưởng khoa, Bác sĩ điều trị */
    @Column(name = "chuc_vu", length = 100)
    private String chucVu;
}
