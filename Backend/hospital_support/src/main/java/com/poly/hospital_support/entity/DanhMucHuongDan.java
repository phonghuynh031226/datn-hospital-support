package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "danh_muc_huong_dan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMucHuongDan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_danh_muc", nullable = false, length = 150)
    private String tenDanhMuc;

    @Column(name = "thu_tu", columnDefinition = "INT DEFAULT 0")
    private Integer thuTu;
}
