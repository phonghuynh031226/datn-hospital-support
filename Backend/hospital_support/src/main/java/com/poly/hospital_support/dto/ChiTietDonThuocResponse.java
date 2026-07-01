package com.poly.hospital_support.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDonThuocResponse {
    private Integer id;
    private Integer thuocId;
    private String tenThuoc;
    private String donViTinh;
    private BigDecimal donGia;
    private Integer soLuong;
    private Integer soLuongThucPhat;
    private String huongDanSuDung;
    private String loai;
}
