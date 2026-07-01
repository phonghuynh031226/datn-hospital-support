package com.poly.hospital_support.service;

import com.poly.hospital_support.dto.AuthResponse;
import com.poly.hospital_support.dto.LoginRequest;
import com.poly.hospital_support.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse updateProfile(Integer id, RegisterRequest request);
    void forgotPassword(String username);
    void resetPassword(String username, String otp, String newPassword);
    void changePassword(Integer id, String oldPassword, String newPassword);
}
