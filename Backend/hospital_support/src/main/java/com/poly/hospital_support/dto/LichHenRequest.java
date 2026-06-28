package com.poly.hospital_support.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichHenRequest {
    private Integer patientId;
    private Integer doctorId;
    private LocalDate ngayHen;
    private String khungGio;
    private String trieuChung;
}
