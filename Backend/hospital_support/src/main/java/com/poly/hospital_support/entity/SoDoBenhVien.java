package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "so_do_benh_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoDoBenhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_tang", nullable = false, length = 100)
    private String tenTang;
}
