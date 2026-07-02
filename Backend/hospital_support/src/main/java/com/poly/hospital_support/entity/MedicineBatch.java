package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "lo_thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String soLo;

    private LocalDate ngaySanXuat;

    private LocalDate hanSuDung;

    private Integer soLuongNhap;

    private Integer soLuongConLai;

    @ManyToOne
    @JoinColumn(name = "ma_thuoc")
    private Medicine medicine;
}