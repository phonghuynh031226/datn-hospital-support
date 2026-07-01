package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cau_hoi_thuong_gap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CauHoiThuongGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_danh_muc")
    private DanhMucFaq danhMuc;

    @Column(name = "cau_hoi", nullable = false, length = 400)
    private String cauHoi;

    @Column(name = "tra_loi", nullable = false, columnDefinition = "TEXT")
    private String traLoi;
}
