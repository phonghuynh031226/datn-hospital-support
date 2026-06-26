package com.poly.hospital_support.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonThuocResponse {
    private Integer id;
    private Integer benhAnId;
    private String bacSiName;
    private String benhNhanName;
    private String trangThai;
    private BigDecimal tongTien;
    private LocalDateTime ngayTao;
    private List<ChiTietDonThuocResponse> details;
}
