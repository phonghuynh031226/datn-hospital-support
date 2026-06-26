package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vi_tri_phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViTriPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_so_do", nullable = false)
    private SoDoBenhVien soDo;

    @Column(name = "so_phong", length = 20)
    private String soPhong;

    @Column(name = "ten_phong", nullable = false, length = 100)
    private String tenPhong;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    /** Ví dụ: 'CAP_CUU', 'TIEP_DON', 'NHA_THUOC' */
    @Column(name = "loai_phong", length = 20)
    private String loaiPhong;
}
