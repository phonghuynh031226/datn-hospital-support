package com.poly.hospital_support.impl;

import com.poly.hospital_support.dto.AuthResponse;
import com.poly.hospital_support.dto.LoginRequest;
import com.poly.hospital_support.dto.RegisterRequest;
import com.poly.hospital_support.entity.BenhNhan;
import com.poly.hospital_support.entity.NhanVien;
import com.poly.hospital_support.entity.TaiKhoan;
import com.poly.hospital_support.repository.BenhNhanRepository;
import com.poly.hospital_support.repository.NhanVienRepository;
import com.poly.hospital_support.repository.TaiKhoanRepository;
import com.poly.hospital_support.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final com.poly.hospital_support.service.EmailService emailService;

    private final java.util.Map<String, OtpData> otpCache = new java.util.concurrent.ConcurrentHashMap<>();

    private static class OtpData {
        String otp;
        long expiryTime;
        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate if phone already exists
        if (taiKhoanRepository.findBySoDienThoai(request.getPhone()).isPresent()) {
            throw new RuntimeException("Sá»‘ Ä‘iá»‡n thoáº¡i nÃ y Ä‘Ã£ Ä‘Æ°á»£c Ä‘Äƒng kÃ½ tÃ i khoáº£n!");
        }

        // Validate if email already exists
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() 
            && taiKhoanRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email nÃ y Ä‘Ã£ Ä‘Æ°á»£c Ä‘Äƒng kÃ½ tÃ i khoáº£n!");
        }

        // Hash password using BCrypt
        String hashedPw = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // Create TaiKhoan
        TaiKhoan tk = TaiKhoan.builder()
                .soDienThoai(request.getPhone())
                .email(request.getEmail() != null && !request.getEmail().trim().isEmpty() ? request.getEmail() : null)
                .matKhau(hashedPw)
                .vaiTro("BENH_NHAN")
                .trangThai((byte) 1)
                .build();
        tk = taiKhoanRepository.save(tk);

        // Create BenhNhan profile
        BenhNhan bn = BenhNhan.builder()
                .taiKhoan(tk)
                .hoTen(request.getFullName())
                .gioiTinh(request.getGender() != null ? request.getGender() : "Nam")
                .diaChi(request.getAddress())
                .ngaySinh(request.getNgaySinh())
                .build();
        benhNhanRepository.save(bn);

        return AuthResponse.builder()
                .id(tk.getId())
                .phone(tk.getSoDienThoai())
                .email(tk.getEmail())
                .role(tk.getVaiTro())
                .fullName(bn.getHoTen())
                .gender(bn.getGioiTinh())
                .address(bn.getDiaChi())
                .ngaySinh(bn.getNgaySinh())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Find TaiKhoan by phone or email
        TaiKhoan tk = taiKhoanRepository.findBySoDienThoaiOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new RuntimeException("TÃ i khoáº£n hoáº·c máº­t kháº©u khÃ´ng chÃ­nh xÃ¡c!"));

        // Check if account is active
        if (tk.getTrangThai() != null && tk.getTrangThai() == 0) {
            throw new RuntimeException("TÃ i khoáº£n nÃ y Ä‘Ã£ bá»‹ khÃ³a!");
        }

        // Validate password
        if (!BCrypt.checkpw(request.getPassword(), tk.getMatKhau())) {
            throw new RuntimeException("TÃ i khoáº£n hoáº·c máº­t kháº©u khÃ´ng chÃ­nh xÃ¡c!");
        }

        // Fetch corresponding profile details
        String fullName = "";
        String gender = "";
        String address = "";
        java.time.LocalDate ngaySinh = null;

        if ("BENH_NHAN".equalsIgnoreCase(tk.getVaiTro())) {
            Optional<BenhNhan> bnOpt = benhNhanRepository.findByTaiKhoan(tk);
            if (bnOpt.isPresent()) {
                fullName = bnOpt.get().getHoTen();
                gender = bnOpt.get().getGioiTinh();
                address = bnOpt.get().getDiaChi();
                ngaySinh = bnOpt.get().getNgaySinh();
            }
        } else {
            Optional<NhanVien> nvOpt = nhanVienRepository.findByTaiKhoan(tk);
            if (nvOpt.isPresent()) {
                fullName = nvOpt.get().getHoTen();
                gender = nvOpt.get().getGioiTinh();
                address = nvOpt.get().getDiaChi();
                ngaySinh = nvOpt.get().getNgaySinh();
            }
        }

        return AuthResponse.builder()
                .id(tk.getId())
                .phone(tk.getSoDienThoai())
                .email(tk.getEmail())
                .role(tk.getVaiTro())
                .fullName(fullName)
                .gender(gender)
                .address(address)
                .ngaySinh(ngaySinh)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse updateProfile(Integer id, RegisterRequest request) {
        TaiKhoan tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KhÃ´ng tÃ¬m tháº¥y tÃ i khoáº£n tÆ°Æ¡ng á»©ng!"));

        // Check if new phone is taken by another account
        Optional<TaiKhoan> phoneCheck = taiKhoanRepository.findBySoDienThoai(request.getPhone());
        if (phoneCheck.isPresent() && !phoneCheck.get().getId().equals(id)) {
            throw new RuntimeException("Sá»‘ Ä‘iá»‡n thoáº¡i nÃ y Ä‘Ã£ Ä‘Æ°á»£c sá»­ dá»¥ng bá»Ÿi tÃ i khoáº£n khÃ¡c!");
        }

        // Check if new email is taken by another account
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            Optional<TaiKhoan> emailCheck = taiKhoanRepository.findByEmail(request.getEmail());
            if (emailCheck.isPresent() && !emailCheck.get().getId().equals(id)) {
                throw new RuntimeException("Email nÃ y Ä‘Ã£ Ä‘Æ°á»£c sá»­ dá»¥ng bá»Ÿi tÃ i khoáº£n khÃ¡c!");
            }
        }

        tk.setSoDienThoai(request.getPhone());
        tk.setEmail(request.getEmail() != null && !request.getEmail().trim().isEmpty() ? request.getEmail() : null);
        taiKhoanRepository.save(tk);

        String fullName = request.getFullName();
        String gender = request.getGender();
        String address = request.getAddress();
        java.time.LocalDate ngaySinh = request.getNgaySinh();

        if ("BENH_NHAN".equalsIgnoreCase(tk.getVaiTro())) {
            BenhNhan bn = benhNhanRepository.findByTaiKhoan(tk)
                    .orElseGet(() -> BenhNhan.builder().taiKhoan(tk).build());
            bn.setHoTen(fullName);
            bn.setGioiTinh(gender);
            bn.setDiaChi(address);
            bn.setNgaySinh(ngaySinh);
            benhNhanRepository.save(bn);
        } else {
            NhanVien nv = nhanVienRepository.findByTaiKhoan(tk)
                    .orElseGet(() -> NhanVien.builder().taiKhoan(tk).build());
            nv.setHoTen(fullName);
            nv.setGioiTinh(gender);
            nv.setDiaChi(address);
            nv.setNgaySinh(ngaySinh);
            nhanVienRepository.save(nv);
        }

        return AuthResponse.builder()
                .id(tk.getId())
                .phone(tk.getSoDienThoai())
                .email(tk.getEmail())
                .role(tk.getVaiTro())
                .fullName(fullName)
                .gender(gender)
                .address(address)
                .ngaySinh(ngaySinh)
                .build();
    }

    @Override
    public void forgotPassword(String username) {
        TaiKhoan tk = taiKhoanRepository.findBySoDienThoaiOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại trên hệ thống!"));

        String email = tk.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Tài khoản của bạn chưa được liên kết với email để nhận OTP!");
        }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new java.util.Random().nextInt(1000000));
        otpCache.put(username, new OtpData(otp, System.currentTimeMillis() + 5 * 60 * 1000)); // 5 mins

        // Send Email
        String subject = "Mã OTP khôi phục mật khẩu - Hospital Support";
        String body = "Chào bạn,\n\nMã OTP khôi phục mật khẩu của bạn là: " + otp + "\nMã này có hiệu lực trong vòng 5 phút.\n\nTrân trọng!";
        emailService.sendSimpleEmail(email, subject, body);
    }

    @Override
    @Transactional
    public void resetPassword(String username, String otp, String newPassword) {
        OtpData data = otpCache.get(username);
        if (data == null) {
            throw new RuntimeException("Yêu cầu khôi phục mật khẩu không hợp lệ!");
        }

        if (System.currentTimeMillis() > data.expiryTime) {
            otpCache.remove(username);
            throw new RuntimeException("Mã OTP đã hết hạn sử dụng!");
        }

        if (!data.otp.equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác!");
        }

        // OTP Valid
        otpCache.remove(username);

        TaiKhoan tk = taiKhoanRepository.findBySoDienThoaiOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        String hashedPw = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        tk.setMatKhau(hashedPw);
        taiKhoanRepository.save(tk);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, String oldPassword, String newPassword) {
        TaiKhoan tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản tương ứng!"));

        if (!BCrypt.checkpw(oldPassword, tk.getMatKhau())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }

        String hashedPw = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        tk.setMatKhau(hashedPw);
        taiKhoanRepository.save(tk);
    }
}

