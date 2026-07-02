package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String tenThuoc;

    private String hoatChat;

    private String hamLuong;

    private String donVi;

    private String dangThuoc;

    private Integer soLuongTon;

    private String ghiChu;
}