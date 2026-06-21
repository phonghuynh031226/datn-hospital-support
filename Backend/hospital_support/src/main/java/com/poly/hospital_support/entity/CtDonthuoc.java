package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_don_thuoc")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CtDonthuoc extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ma_don_thuoc")
    private Donthuoc donThuoc; // Liên kết ngược lại với Đơn thuốc cha

    @ManyToOne
    @JoinColumn(name = "ma_thuoc")
    private Medicine medicine; // Liên kết với danh mục thuốc

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "huong_dan_su_dung")
    private String huongDanSuDung;
}