package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "danh_muc_faq")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMucFaq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_danh_muc", nullable = false, length = 150)
    private String tenDanhMuc;
}
