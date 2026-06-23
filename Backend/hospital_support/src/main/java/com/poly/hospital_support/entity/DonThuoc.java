package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "don_thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "ma_benh_an", unique = true)
    private BenhAn benhAn;

    @ManyToOne
    @JoinColumn(name = "ma_bac_si")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "ma_benh_nhan")
    private BenhNhan benhNhan;

    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @OneToMany(mappedBy = "donThuoc", cascade = CascadeType.ALL)
    private List<CTDonThuoc> ctDonThuocList;
}