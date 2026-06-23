package com.poly.hospital_support.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Integer id;
    private String phone;
    private String email;
    private String role; // 'BENH_NHAN', 'DIEU_DUONG', 'BAC_SI', 'DUOC_SI', 'THU_KHO'
    private String fullName;
    private String gender;
    private String address;
    private java.time.LocalDate ngaySinh;
}
