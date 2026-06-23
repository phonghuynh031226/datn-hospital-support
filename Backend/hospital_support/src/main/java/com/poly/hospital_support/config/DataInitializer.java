package com.poly.hospital_support.config;

import com.poly.hospital_support.entity.BenhNhan;
import com.poly.hospital_support.entity.NhanVien;
import com.poly.hospital_support.entity.TaiKhoan;
import com.poly.hospital_support.repository.BenhNhanRepository;
import com.poly.hospital_support.repository.NhanVienRepository;
import com.poly.hospital_support.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed default accounts if tai_khoan is empty
        if (taiKhoanRepository.count() == 0) {
            String defaultPw = BCrypt.hashpw("123456", BCrypt.gensalt());

            // 1. Patient
            TaiKhoan tkPatient = TaiKhoan.builder()
                    .soDienThoai("0901234567")
                    .email("patient@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("BENH_NHAN")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkPatient);

            BenhNhan bn = BenhNhan.builder()
                    .taiKhoan(tkPatient)
                    .hoTen("Nguyễn Văn A")
                    .gioiTinh("Nam")
                    .diaChi("Quận 1, TP. Hồ Chí Minh")
                    .ngaySinh(LocalDate.of(1990, 5, 10))
                    .build();
            benhNhanRepository.save(bn);

            // 2. Nurse
            TaiKhoan tkNurse = TaiKhoan.builder()
                    .soDienThoai("0907654321")
                    .email("nurse@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("DIEU_DUONG")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkNurse);

            NhanVien nvNurse = NhanVien.builder()
                    .taiKhoan(tkNurse)
                    .hoTen("Điều dưỡng Nguyễn Thị Mai")
                    .gioiTinh("Nu")
                    .diaChi("Hà Nội")
                    .ngaySinh(LocalDate.of(1995, 8, 20))
                    .ngayLam(LocalDate.of(2020, 1, 15))
                    .build();
            nhanVienRepository.save(nvNurse);

            // 3. Doctor
            TaiKhoan tkDoctor = TaiKhoan.builder()
                    .soDienThoai("0909998888")
                    .email("doctor@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("BAC_SI")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkDoctor);

            NhanVien nvDoctor = NhanVien.builder()
                    .taiKhoan(tkDoctor)
                    .hoTen("PGS.TS Nguyễn Văn An")
                    .gioiTinh("Nam")
                    .diaChi("Hồ Chí Minh")
                    .ngaySinh(LocalDate.of(1980, 11, 5))
                    .ngayLam(LocalDate.of(2010, 5, 20))
                    .build();
            nhanVienRepository.save(nvDoctor);

            // 4. Pharmacist
            TaiKhoan tkPharmacist = TaiKhoan.builder()
                    .soDienThoai("0908887777")
                    .email("pharmacist@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("DUOC_SI")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkPharmacist);

            NhanVien nvPharmacist = NhanVien.builder()
                    .taiKhoan(tkPharmacist)
                    .hoTen("Dược sĩ Trần Thanh Thủy")
                    .gioiTinh("Nu")
                    .diaChi("Đà Nẵng")
                    .ngaySinh(LocalDate.of(1988, 3, 12))
                    .ngayLam(LocalDate.of(2015, 6, 1))
                    .build();
            nhanVienRepository.save(nvPharmacist);

            // 5. Warehouse Keeper
            TaiKhoan tkWarehouse = TaiKhoan.builder()
                    .soDienThoai("0907776666")
                    .email("warehouse@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("THU_KHO")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkWarehouse);

            NhanVien nvWarehouse = NhanVien.builder()
                    .taiKhoan(tkWarehouse)
                    .hoTen("Thủ kho Phạm Minh Tuấn")
                    .gioiTinh("Nam")
                    .diaChi("Cần Thơ")
                    .ngaySinh(LocalDate.of(1992, 9, 30))
                    .ngayLam(LocalDate.of(2018, 10, 10))
                    .build();
            nhanVienRepository.save(nvWarehouse);

            System.out.println(">>> Database seeded with default accounts (password: 123456) successfully!");
        }
    }
}
