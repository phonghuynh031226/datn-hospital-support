package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.YeuCauLienHe;
import com.poly.hospital_support.repository.YeuCauLienHeRepository;
import com.poly.hospital_support.service.EmailService;
import com.poly.hospital_support.service.YeuCauLienHeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YeuCauLienHeServiceImpl implements YeuCauLienHeService {

    private final YeuCauLienHeRepository yeuCauLienHeRepository;
    private final EmailService emailService;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Override
    @Transactional
    public YeuCauLienHe submitInquiry(YeuCauLienHe inquiry) {
        YeuCauLienHe saved = yeuCauLienHeRepository.save(inquiry);

        // Send email alert to admin
        try {
            String subject = "Yêu cầu liên hệ mới: " + saved.getChuDe();
            String body = "Bạn nhận được yêu cầu liên hệ mới từ khách hàng:\n\n"
                    + "Họ tên: " + saved.getHoTen() + "\n"
                    + "Email: " + saved.getEmail() + "\n"
                    + "Số điện thoại: " + saved.getSoDienThoai() + "\n"
                    + "Chủ đề: " + saved.getChuDe() + "\n"
                    + "Nội dung:\n" + saved.getNoiDung() + "\n\n"
                    + "Hệ thống tự động thông báo.";
            emailService.sendSimpleEmail(adminEmail, subject, body);
        } catch (Exception e) {
            System.err.println("Could not send contact inquiry alert email. Error: " + e.getMessage());
        }

        return saved;
    }
}
