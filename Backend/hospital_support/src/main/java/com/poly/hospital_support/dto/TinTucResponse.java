package com.poly.hospital_support.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TinTucResponse {

    private Integer id;

    private String tieuDe;

    private String tomTat;

    private String noiDung;

    private String hinhAnh;

    private String tacGia;

    private String danhMuc;

    private Boolean daGhim;

    private LocalDateTime ngayTao;

    private LocalDateTime ngayCapNhat;
}