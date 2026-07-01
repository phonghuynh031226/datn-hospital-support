package com.poly.hospital_support.service;

public interface EmailService {
    void sendSimpleEmail(String toEmail, String subject, String body);
}
