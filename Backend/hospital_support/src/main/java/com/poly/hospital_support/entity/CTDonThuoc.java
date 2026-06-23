package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_don_thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTDonThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ma_don_thuoc")
    private DonThuoc donThuoc;

    @ManyToOne
    @JoinColumn(name = "ma_thuoc")
    private Thuoc medicine;

    private Integer soLuong;
    private String huongDanSuDung;
}