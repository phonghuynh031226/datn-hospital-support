package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity // <--- BẮT BUỘC PHẢI CÓ DÒNG NÀY
@Table(name = "thuoc") // <--- Tên bảng phải khớp với database
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Thuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String tenThuoc;
    private String donViTinh;
    private Double donGia;
    private Integer soLuongTon;
    private Integer soLuongToiThieu;
    private LocalDate hanSuDung;
}