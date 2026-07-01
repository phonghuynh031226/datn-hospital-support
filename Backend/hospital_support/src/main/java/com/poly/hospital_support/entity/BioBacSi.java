package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bio_bacsi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BioBacSi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tai_khoan", referencedColumnName = "id")
    private TaiKhoan taiKhoan;

    @Column(name = "noidung", columnDefinition = "LONGTEXT")
    private String noiDung;
}
