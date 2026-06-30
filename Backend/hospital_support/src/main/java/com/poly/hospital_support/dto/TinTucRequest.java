package com.poly.hospital_support.dto;

import lombok.Data;

@Data
public class TinTucRequest {

    private String tieuDe;

    private String tomTat;

    private String noiDung;

    private String hinhAnh;

    private String tacGia;

    private String danhMuc;

    private Boolean daGhim;
}