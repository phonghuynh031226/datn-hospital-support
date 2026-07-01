package com.poly.hospital_support.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String email;

    private String gender; // 'Nam', 'Nu', 'Khac'

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    private String address;

    private java.time.LocalDate ngaySinh;
}
