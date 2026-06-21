package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tai_khoan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "mat_khau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "vai_tro", nullable = false, length = 30)
    private String vaiTro; // 'BENH_NHAN', 'DIEU_DUONG', 'BAC_SI', 'DUOC_SI', 'THU_KHO'

    @Column(name = "trang_thai", columnDefinition = "TINYINT DEFAULT 1")
    private Byte trangThai;

    @Column(name = "ngay_tao", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
