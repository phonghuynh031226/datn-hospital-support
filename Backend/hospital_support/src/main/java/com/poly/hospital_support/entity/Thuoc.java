package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Thuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_thuoc", nullable = false, length = 150)
    private String tenThuoc;

    @Column(name = "don_vi_tinh", nullable = false, length = 20)
    private String donViTinh;

    @Column(name = "don_gia", columnDefinition = "DECIMAL(12,2) DEFAULT 0")
    private BigDecimal donGia;

    /** Tồn kho vật lý thực tế (tổng tất cả lô) */
    @Column(name = "so_luong_ton", columnDefinition = "INT DEFAULT 0")
    private Integer soLuongTon;

    /** Tồn khả dụng = ton - đang Hold bởi đơn thuốc */
    @Column(name = "so_luong_kha_dung", columnDefinition = "INT DEFAULT 0")
    private Integer soLuongKhaDung;

    /** Ngưỡng cảnh báo sắp hết hàng */
    @Column(name = "so_luong_toi_thieu", columnDefinition = "INT DEFAULT 10")
    private Integer soLuongToiThieu;
}
