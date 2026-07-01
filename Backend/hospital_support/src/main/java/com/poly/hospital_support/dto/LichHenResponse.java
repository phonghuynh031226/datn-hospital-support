package com.poly.hospital_support.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichHenResponse {
    private Integer id;
    private Integer patientId;
    private String patientName;
    private Integer doctorId;
    private String doctorName;
    private LocalDate ngayHen;
    private String khungGio;
    private String trieuChung;
    private String trangThai;
    private String nguonTao;
    private LocalDateTime ngayTao;
}
