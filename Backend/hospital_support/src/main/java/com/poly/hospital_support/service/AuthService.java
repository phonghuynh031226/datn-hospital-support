package com.poly.hospital_support.service;

<<<<<<< HEAD
public class AuthService {
=======
import com.poly.hospital_support.dto.AuthResponse;
import com.poly.hospital_support.dto.LoginRequest;
import com.poly.hospital_support.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse updateProfile(Integer id, RegisterRequest request);
>>>>>>> 8e0f29c6c218516f95ecee0d5a2d816de8a4ec9f
}
