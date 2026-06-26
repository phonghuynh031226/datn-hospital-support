package com.poly.hospital_support.config;

import com.poly.hospital_support.entity.*;
import com.poly.hospital_support.repository.*;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final GioiThieuRepository gioiThieuRepository;
    private final SoDoBenhVienRepository soDoBenhVienRepository;
    private final ViTriPhongRepository viTriPhongRepository;
    private final KhoaRepository khoaRepository;
    private final BacSiKhoaRepository bacSiKhoaRepository;
    private final BioBacSiRepository bioBacSiRepository;

    @Override
    public void run(String... args) throws Exception {
        String defaultPw = BCrypt.hashpw("123456", BCrypt.gensalt());

        // 1. Seed GioiThieu if empty
        if (gioiThieuRepository.count() == 0) {
            GioiThieu gt = GioiThieu.builder()
                    .id(1)
                    .tieuDe("HỆ THỐNG Y TẾ BỆNH VIỆN ĐA KHOA QUỐC TẾ")
                    .noiDung("<h2 style=\"text-align: center; color: #1e3a8a;\"><strong>HỆ THỐNG Y TẾ BỆNH VIỆN ĐA KHOA QUỐC TẾ</strong></h2>" +
                            "<p style=\"text-align: center; font-style: italic; color: #4b5563;\">Hệ thống y tế hiện đại, uy tín hàng đầu — Nơi sức khỏe của bạn là ưu tiên số một</p>" +
                            "<hr />" +
                            "<p>Bệnh viện Đa khoa Quốc tế tự hào là một trong những cơ sở y tế hàng đầu Việt Nam cung cấp dịch vụ khám chữa bệnh chất lượng cao theo tiêu chuẩn quốc tế. Với triết lý <strong>\"Tận tâm vì sức khỏe người bệnh\"</strong>, chúng tôi không ngừng cải tiến quy trình, đầu tư trang thiết bị công nghệ hiện đại và quy tụ đội ngũ chuyên gia, y bác sĩ đầu ngành nhằm đem lại trải nghiệm y tế hoàn hảo nhất.</p>" +
                            "<p>Bệnh viện được thiết kế theo mô hình bệnh viện thông minh, tối ưu hóa quy trình thủ tục qua cổng thông tin trực tuyến, kết hợp hệ thống khám đa khoa toàn diện và quản lý chuyên nghiệp.</p>")
                    .build();
            gioiThieuRepository.save(gt);
            System.out.println(">>> Seeded GioiThieu successfully!");
        }

        // 2. Seed Khoa if empty
        if (khoaRepository.count() == 0) {
            Khoa k1 = Khoa.builder()
                    .tenKhoa("Khoa Tim Mạch")
                    .slug("tim-mach")
                    .moTaNgan("Chuyên khoa tim mạch kỹ thuật cao, tầm soát và điều trị các bệnh lý tim mạch.")
                    .noiDungGioiThieu("Khoa Tim mạch là một trong những khoa mũi nhọn tại bệnh viện chúng tôi, cung cấp dịch vụ tầm soát bệnh mạch vành, tăng huyết áp, suy tim và can thiệp mạch vành chuyên sâu.")
                    .trangThietBi("Máy siêu âm tim doppler 4D, máy chụp mạch số hóa xóa nền DSA.")
                    .thuTu(1)
                    .trangThai((byte) 1)
                    .build();
            khoaRepository.save(k1);

            Khoa k2 = Khoa.builder()
                    .tenKhoa("Khoa Nội Tổng Quát")
                    .slug("noi-tong-quat")
                    .moTaNgan("Chẩn đoán và điều trị không phẫu thuật các bệnh lý nội khoa đa dạng.")
                    .noiDungGioiThieu("Khoa Nội tổng quát chuyên điều trị các bệnh lý đường hô hấp, tiêu hóa, nội tiết, tiểu đường, gan mật ở người lớn.")
                    .trangThietBi("Hệ thống đo đa ký giấc ngủ, máy điện tim 12 chuyển đạo.")
                    .thuTu(2)
                    .trangThai((byte) 1)
                    .build();
            khoaRepository.save(k2);

            Khoa k3 = Khoa.builder()
                    .tenKhoa("Khoa Ngoại Khoa")
                    .slug("ngoai-khoa")
                    .moTaNgan("Phẫu thuật ngoại khoa tổng quát, ứng dụng kỹ thuật mổ nội soi tiên tiến.")
                    .noiDungGioiThieu("Khoa Ngoại khoa thực hiện các dịch vụ phẫu thuật nội soi ổ bụng, phẫu thuật trĩ, ruột thừa, sỏi mật và chấn thương chỉnh hình.")
                    .trangThietBi("Hệ thống phòng mổ áp lực dương tiêu chuẩn quốc tế, giàn máy nội soi Karl Storz.")
                    .thuTu(3)
                    .trangThai((byte) 1)
                    .build();
            khoaRepository.save(k3);

            Khoa k4 = Khoa.builder()
                    .tenKhoa("Khoa Sản Phụ Khoa")
                    .slug("san-phu-khoa")
                    .moTaNgan("Chăm sóc sức khỏe phụ nữ, khám thai sản trọn gói và hỗ trợ sinh sản.")
                    .noiDungGioiThieu("Khoa Sản phụ khoa cung cấp dịch vụ thai sản toàn diện, theo dõi thai nghén chất lượng cao, chẩn đoán trước sinh và điều trị các bệnh lý phụ khoa.")
                    .trangThietBi("Máy siêu âm sản khoa Voluson E10 thế hệ mới, bàn sinh đa năng hiện đại.")
                    .thuTu(4)
                    .trangThai((byte) 1)
                    .build();
            khoaRepository.save(k4);

            Khoa k5 = Khoa.builder()
                    .tenKhoa("Khoa Nhi Khoa")
                    .slug("nhi-khoa")
                    .moTaNgan("Khám và điều trị toàn diện các bệnh lý ở trẻ sơ sinh và trẻ nhỏ.")
                    .noiDungGioiThieu("Khoa Nhi chuyên khám, tiêm chủng ngừa và điều trị nội trú cho trẻ nhỏ với đội ngũ y bác sĩ thân thiện, cơ sở vật chất vui chơi sinh động.")
                    .trangThietBi("Lồng ấp sơ sinh, máy thở oxy dòng cao HFNC cho trẻ em.")
                    .thuTu(5)
                    .trangThai((byte) 1)
                    .build();
            khoaRepository.save(k5);

            Khoa k6 = Khoa.builder()
                    .tenKhoa("Khoa Mắt")
                    .slug("mat")
                    .moTaNgan("Khám mắt toàn diện, đo khúc xạ và phẫu thuật đục thủy tinh thể phaco.")
                    .noiDungGioiThieu("Khoa Mắt hỗ trợ điều trị đục thủy tinh thể, cận viễn loạn thị, kiểm tra đáy mắt tiểu đường và phẫu thuật khúc xạ.")
                    .trangThietBi("Máy phẫu thuật Phaco Centurion, máy chụp cắt lớp võng mạc OCT.")
                    .thuTu(6)
                    .trangThai((byte) 1)
                    .build();
            khoaRepository.save(k6);

            System.out.println(">>> Seeded Khoa successfully!");
        }

        // 3. Seed SoDoBenhVien and ViTriPhong if empty
        if (soDoBenhVienRepository.count() == 0) {
            SoDoBenhVien s1 = SoDoBenhVien.builder().tenTang("Tầng Trệt (G) - Đón Tiếp & Cấp Cứu").build();
            soDoBenhVienRepository.save(s1);
            SoDoBenhVien s2 = SoDoBenhVien.builder().tenTang("Tầng 1 - Phòng Khám Chuyên Khoa").build();
            soDoBenhVienRepository.save(s2);
            SoDoBenhVien s3 = SoDoBenhVien.builder().tenTang("Tầng 2 - Cận Lâm Sàng & Xét Nghiệm").build();
            soDoBenhVienRepository.save(s3);

            // Rooms G
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s1).soPhong("P.01").tenPhong("Phòng Tiếp Đón & Đăng Ký").moTa("Nơi đón tiếp người bệnh và hướng dẫn thủ tục khám chữa bệnh.").loaiPhong("TIEP_DON").build());
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s1).soPhong("CC.01").tenPhong("Phòng Cấp Cứu Đa Khoa").moTa("Tiếp nhận cấp cứu khẩn cấp 24/7.").loaiPhong("CAP_CUU").build());
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s1).soPhong("NT.01").tenPhong("Nhà Thuốc Bệnh Viện").moTa("Cấp phát thuốc bảo hiểm và bán lẻ thuốc.").loaiPhong("NHA_THUOC").build());

            // Rooms 1
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s2).soPhong("P.101").tenPhong("Phòng Khám Tim Mạch").moTa("Khám bệnh lý tim mạch, đo điện tim.").loaiPhong("BUONG_BENH").build());
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s2).soPhong("P.102").tenPhong("Phòng Khám Nội Tổng Quát").moTa("Khám bệnh lý nội khoa chung.").loaiPhong("BUONG_BENH").build());
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s2).soPhong("P.103").tenPhong("Phòng Khám Sản Phụ Khoa").moTa("Theo dõi thai sản, khám phụ khoa định kỳ.").loaiPhong("BUONG_BENH").build());
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s2).soPhong("P.104").tenPhong("Phòng Khám Nhi Khoa").moTa("Chăm sóc sức khỏe trẻ em.").loaiPhong("BUONG_BENH").build());

            // Rooms 2
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s3).soPhong("P.201").tenPhong("Phòng Xét Nghiệm Máu & Sinh Hóa").moTa("Thực hiện phân tích xét nghiệm máu và sinh hóa.").loaiPhong("CHAN_DOAN").build());
            viTriPhongRepository.save(ViTriPhong.builder().soDo(s3).soPhong("P.202").tenPhong("Phòng Chụp X-Quang & CT Scanner").moTa("Chụp phim chẩn đoán hình ảnh kỹ thuật số.").loaiPhong("CHAN_DOAN").build());

            System.out.println(">>> Seeded SoDoBenhVien & ViTriPhong successfully!");
        }

        // 4. Seed default accounts, doctors and link departments
        if (taiKhoanRepository.count() == 0) {
            // Seed Patient
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

            // Seed Nurse
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

            // Fetch departments for mapping doctors
            List<Khoa> depts = khoaRepository.findAll();
            Khoa kCardio = depts.stream().filter(k -> k.getSlug().equals("tim-mach")).findFirst().orElse(null);
            Khoa kPediatric = depts.stream().filter(k -> k.getSlug().equals("nhi-khoa")).findFirst().orElse(null);
            Khoa kSurgeon = depts.stream().filter(k -> k.getSlug().equals("ngoai-khoa")).findFirst().orElse(null);
            Khoa kObstetric = depts.stream().filter(k -> k.getSlug().equals("san-phu-khoa")).findFirst().orElse(null);

            // Doctor 1: PGS.TS Nguyễn Văn An
            TaiKhoan tkDoctor1 = TaiKhoan.builder()
                    .soDienThoai("0909998888")
                    .email("doctor@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("BAC_SI")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkDoctor1);

            NhanVien nvDoctor1 = NhanVien.builder()
                    .taiKhoan(tkDoctor1)
                    .hoTen("PGS.TS Nguyễn Văn An")
                    .gioiTinh("Nam")
                    .diaChi("Hồ Chí Minh")
                    .ngaySinh(LocalDate.of(1980, 11, 5))
                    .ngayLam(LocalDate.of(2010, 5, 20))
                    .build();
            nhanVienRepository.save(nvDoctor1);

            bioBacSiRepository.save(BioBacSi.builder().taiKhoan(tkDoctor1).noiDung("PGS.TS Nguyễn Văn An là một chuyên gia đầu ngành trong lĩnh vực Tim mạch, có 25 năm kinh nghiệm công tác và nghiên cứu lâm sàng. Ông từng giữ vai trò phó chủ tịch hội tim mạch học Việt Nam.").build());
            if (kCardio != null) {
                bacSiKhoaRepository.save(BacSiKhoa.builder().nhanVien(nvDoctor1).khoa(kCardio).chucVu("Trưởng khoa").build());
            }

            // Doctor 2: ThS.BS Lê Thị Hồng
            TaiKhoan tkDoctor2 = TaiKhoan.builder()
                    .soDienThoai("0901112222")
                    .email("doctor2@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("BAC_SI")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkDoctor2);

            NhanVien nvDoctor2 = NhanVien.builder()
                    .taiKhoan(tkDoctor2)
                    .hoTen("ThS.BS Lê Thị Hồng")
                    .gioiTinh("Nu")
                    .diaChi("Hà Nội")
                    .ngaySinh(LocalDate.of(1985, 3, 15))
                    .ngayLam(LocalDate.of(2013, 9, 1))
                    .build();
            nhanVienRepository.save(nvDoctor2);

            bioBacSiRepository.save(BioBacSi.builder().taiKhoan(tkDoctor2).noiDung("ThS.BS Lê Thị Hồng là bác sĩ chuyên môn vững vàng, tốt nghiệp thạc sĩ sản phụ khoa Đại học Y Dược. Bà luôn tận tâm tư vấn hỗ trợ chăm sóc thai sản.").build());
            if (kObstetric != null) {
                bacSiKhoaRepository.save(BacSiKhoa.builder().nhanVien(nvDoctor2).khoa(kObstetric).chucVu("Bác sĩ điều trị").build());
            }

            // Doctor 3: TS.BS Trần Hữu Quân
            TaiKhoan tkDoctor3 = TaiKhoan.builder()
                    .soDienThoai("0903334444")
                    .email("doctor3@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("BAC_SI")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkDoctor3);

            NhanVien nvDoctor3 = NhanVien.builder()
                    .taiKhoan(tkDoctor3)
                    .hoTen("TS.BS Trần Hữu Quân")
                    .gioiTinh("Nam")
                    .diaChi("Đà Nẵng")
                    .ngaySinh(LocalDate.of(1978, 6, 20))
                    .ngayLam(LocalDate.of(2008, 12, 10))
                    .build();
            nhanVienRepository.save(nvDoctor3);

            bioBacSiRepository.save(BioBacSi.builder().taiKhoan(tkDoctor3).noiDung("TS.BS Trần Hữu Quân chuyên gia phẫu thuật ngoại ổ bụng, nội soi tiêu hóa, đã thực hiện thành công hàng ngàn ca phẫu thuật phức tạp.").build());
            if (kSurgeon != null) {
                bacSiKhoaRepository.save(BacSiKhoa.builder().nhanVien(nvDoctor3).khoa(kSurgeon).chucVu("Trưởng khoa").build());
            }

            // Doctor 4: ThS.BS Nguyễn Minh Thư
            TaiKhoan tkDoctor4 = TaiKhoan.builder()
                    .soDienThoai("0905556666")
                    .email("doctor4@hospital.com")
                    .matKhau(defaultPw)
                    .vaiTro("BAC_SI")
                    .trangThai((byte) 1)
                    .build();
            taiKhoanRepository.save(tkDoctor4);

            NhanVien nvDoctor4 = NhanVien.builder()
                    .taiKhoan(tkDoctor4)
                    .hoTen("ThS.BS Nguyễn Minh Thư")
                    .gioiTinh("Nu")
                    .diaChi("Cần Thơ")
                    .ngaySinh(LocalDate.of(1989, 8, 25))
                    .ngayLam(LocalDate.of(2016, 7, 15))
                    .build();
            nhanVienRepository.save(nvDoctor4);

            bioBacSiRepository.save(BioBacSi.builder().taiKhoan(tkDoctor4).noiDung("ThS.BS Nguyễn Minh Thư chuyên điều trị nhi khoa tổng quát, có nhiều kỹ năng dỗ dành trẻ em giúp quá trình thăm khám diễn ra thoải mái.").build());
            if (kPediatric != null) {
                bacSiKhoaRepository.save(BacSiKhoa.builder().nhanVien(nvDoctor4).khoa(kPediatric).chucVu("Bác sĩ điều trị").build());
            }

            // Seed Pharmacist
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

            // Seed Warehouse Keeper
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

            System.out.println(">>> Database seeded with default accounts, doctors and department mappings successfully!");
        }
    }
}
