package com.poly.hospital_support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CaiDatHeThongRequest {

    @NotBlank(message = "Tên bệnh viện không được để trống")
    @Size(max = 255, message = "Tên bệnh viện tối đa 255 ký tự")
    private String tenBenhVien;

    @Size(max = 1000, message = "Đường dẫn logo quá dài")
    private String duongDanLogo;

    @Size(max = 1000, message = "Đường dẫn banner quá dài")
    private String duongDanBanner;

    @NotBlank(message = "Màu chủ đạo không được để trống")
    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Màu chủ đạo phải có dạng #2563EB"
    )
    private String mauChuDao;
}