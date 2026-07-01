CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

SET NAMES utf8mb4;

CREATE TABLE tai_khoan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    so_dien_thoai VARCHAR(15),
    email VARCHAR(100),       
    mat_khau VARCHAR(255) NOT NULL,
    vai_tro VARCHAR(30) NOT NULL,
    trang_thai TINYINT DEFAULT 1,
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_vai_tro CHECK (vai_tro IN ('BENH_NHAN', 'DIEU_DUONG', 'BAC_SI', 'DUOC_SI', 'THU_KHO', 'GIAM_DOC'))
);

CREATE TABLE benh_nhan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_tai_khoan INT UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    gioi_tinh VARCHAR(10),
    ngay_sinh DATE,
    dia_chi TEXT,
    CONSTRAINT chk_gioi_tinh_bn CHECK (gioi_tinh IN ('Nam', 'Nu', 'Khac')),
    FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(id) ON DELETE CASCADE
);

CREATE TABLE nhan_vien (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_tai_khoan INT UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    gioi_tinh VARCHAR(10),
    dia_chi TEXT,
    ngay_sinh DATE,
    ngay_lam DATE,
    CONSTRAINT chk_gioi_tinh_nv CHECK (gioi_tinh IN ('Nam', 'Nu', 'Khac')),
    FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(id) ON DELETE CASCADE
);

CREATE TABLE bio_bacsi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_tai_khoan INT UNIQUE,
    noidung LONGTEXT,
    FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(id) ON DELETE CASCADE
);



-- Lịch làm việc của bác sĩ: ca nào, phòng nào, ngày nào
-- Điều dưỡng dựa vào đây để biết gán STT vào phòng bác sĩ nào
-- Bác sĩ dựa vào đây để xem ca làm hôm nay của mình
CREATE TABLE ca_lam_viec (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_bac_si INT NOT NULL,
    ngay_lam DATE NOT NULL,

    -- SANG: 7h-12h | CHIEU: 13h-17h | CA_NGAY: 7h-17h
    ca_lam VARCHAR(20) NOT NULL,
    CONSTRAINT chk_ca_lam CHECK (ca_lam IN ('SANG', 'CHIEU', 'CA_NGAY')),

    -- Phòng khám được phân công trong ca (VD: 'Phòng 01', 'Phòng A2')
    phong_kham VARCHAR(50) NOT NULL,

    -- Trạng thái ca làm việc:
    -- CHO_BAT_DAU : ca được phân công, bác sĩ chưa xác nhận
    -- DANG_KHAM   : bác sĩ đã bấm "Bắt đầu ca" → hàng đợi hoạt động
    -- KET_THUC    : bác sĩ kết thúc ca (hoặc hết giờ)
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'CHO_BAT_DAU',
    CONSTRAINT chk_tt_ca CHECK (trang_thai IN ('CHO_BAT_DAU', 'DANG_KHAM', 'KET_THUC')),

    -- 1 bác sĩ chỉ có 1 ca trong 1 ngày
    UNIQUE KEY uq_bacsi_ngay_ca (ma_bac_si, ngay_lam, ca_lam),

    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id) ON DELETE CASCADE
);

CREATE TABLE lich_hen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_benh_nhan INT NOT NULL,
    ma_bac_si INT NOT NULL,
    ngay_hen DATE NOT NULL,
    khung_gio VARCHAR(50),
    trieu_chung TEXT,
    trang_thai VARCHAR(30) DEFAULT 'CHO_XAC_NHAN',
    -- Nguồn tạo lịch hẹn:
    -- 'ONLINE'  : bệnh nhân tự đặt trước qua web
    -- 'TAI_QUAY': điều dưỡng tạo tại quầy cho bệnh nhân chưa có lịch
    nguon_tao VARCHAR(20) NOT NULL DEFAULT 'ONLINE',
    CONSTRAINT chk_nguon_tao CHECK (nguon_tao IN ('ONLINE', 'TAI_QUAY')),
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_trang_thai_lh CHECK (trang_thai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DA_HUY')),
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(id),
    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id)
);

CREATE TABLE benh_an (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_lich_hen INT UNIQUE,
    ma_benh_nhan INT NOT NULL,
    ma_bac_si INT NOT NULL,
    trieu_chung TEXT,
    chan_doan TEXT,
    ket_luan TEXT,
    trang_thai VARCHAR(30) DEFAULT 'CHO_KHAM',
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_trang_thai_ba CHECK (trang_thai IN ('CHO_KHAM', 'DANG_KHAM', 'CHO_XET_NGHIEM', 'CHO_KE_DON', 'HOAN_THANH')),
    FOREIGN KEY (ma_lich_hen) REFERENCES lich_hen(id) ON DELETE SET NULL,
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(id),
    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id)
);

CREATE TABLE yeu_cau_dich_vu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_benh_an INT NOT NULL,
    ma_bac_si INT NOT NULL,
    ten_dich_vu VARCHAR(200) NOT NULL,
    ket_qua TEXT,
    trang_thai VARCHAR(30) DEFAULT 'CHO_THUC_HIEN',
    CONSTRAINT chk_trang_thai_dv CHECK (trang_thai IN ('CHO_THUC_HIEN', 'DA_CO_KET_QUA')),
    FOREIGN KEY (ma_benh_an) REFERENCES benh_an(id) ON DELETE CASCADE,
    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id)
);

CREATE TABLE thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ten_thuoc VARCHAR(150) NOT NULL,
    don_vi_tinh VARCHAR(20) NOT NULL,
    don_gia DECIMAL(12,2) DEFAULT 0,
    so_luong_ton INT DEFAULT 0,          -- Tồn kho vật lý thực tế (tổng tất cả lô)
    so_luong_kha_dung INT DEFAULT 0,     -- Tồn khả dụng = ton - đang Hold bởi đơn thuốc
    so_luong_toi_thieu INT DEFAULT 10    -- Ngưỡng cảnh báo sắp hết hàng
    -- han_su_dung được quản lý theo từng lô trong bảng nhap_kho_thuoc
);

CREATE TABLE don_thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_benh_an INT NOT NULL UNIQUE,
    ma_bac_si INT NOT NULL,
    ma_benh_nhan INT NOT NULL,

    -- Vòng đời đơn thuốc:
    -- CHO_PHAT_THUOC  : bác sĩ vừa xác nhận, kho đã Hold, dược sĩ chờ nhận
    -- DANG_CHUAN_BI  : dược sĩ đang chuẩn bị (có thể thêm thuốc ngoài đơn)
    -- CHO_THANH_TOAN : xong, chờ bệnh nhân tới thanh toán (màn hình gọi số)
    -- YEU_CAU_SUA_DON: kho vật lý thiếu, dược sĩ nhả Hold, đẩy thông báo bác sĩ sửa
    -- DA_PHAT_THUOC  : bệnh nhân đã thanh toán, kho thật được trừ, đơn đóng
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_PHAT_THUOC',
    CONSTRAINT chk_trang_thai_dt CHECK (trang_thai IN (
        'CHO_PHAT_THUOC',
        'DANG_CHUAN_BI',
        'CHO_THANH_TOAN',
        'YEU_CAU_SUA_DON',
        'DA_PHAT_THUOC'
    )),

    tong_tien DECIMAL(12,2) DEFAULT 0,   -- Tổng tiền cuối (thuốc trong đơn + ngoài đơn)
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (ma_benh_an) REFERENCES benh_an(id) ON DELETE CASCADE,
    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id),
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(id)
);

CREATE TABLE chi_tiet_don_thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_don_thuoc INT NOT NULL,
    ma_thuoc INT NOT NULL,

    so_luong INT NOT NULL,               -- Số lượng bác sĩ yêu cầu (dùng để Hold)
    so_luong_thuc_phat INT,              -- Số lượng dược sĩ thực tế phát (điền khi chuẩn bị)
    huong_dan_su_dung VARCHAR(255),

    -- TRONG_DON : dòng do bác sĩ kê (Hold ngay khi tạo đơn)
    -- NGOAI_DON : dược sĩ thêm tại quầy (Hold khi dược sĩ bấm thêm)
    loai VARCHAR(20) NOT NULL DEFAULT 'TRONG_DON',
    CONSTRAINT chk_loai_thuoc CHECK (loai IN ('TRONG_DON', 'NGOAI_DON')),

    FOREIGN KEY (ma_don_thuoc) REFERENCES don_thuoc(id) ON DELETE CASCADE,
    FOREIGN KEY (ma_thuoc) REFERENCES thuoc(id)
);

-- Quản lý từng lô thuốc nhập kho
-- Mỗi lần nhập hàng tạo 1 bản ghi lô riêng với số lô, hạn sử dụng riêng
CREATE TABLE nhap_kho_thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_thuoc INT NOT NULL,
    ma_nhan_vien INT NOT NULL,           -- Nhân viên kho thực hiện nhập

    so_lo VARCHAR(50) NOT NULL,          -- Số lô sản xuất (VD: LOT-2024-001)
    so_luong_nhap INT NOT NULL,          -- Số lượng nhập ban đầu
    so_luong_con_lai INT NOT NULL,       -- Còn lại trong lô (giảm khi phát thuốc)
    don_gia_nhap DECIMAL(12,2),
    han_su_dung DATE NOT NULL,           -- Hạn sử dụng của lô này
    ngay_nhap DATETIME DEFAULT CURRENT_TIMESTAMP,

    trang_thai VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG',
    CONSTRAINT chk_tt_lo CHECK (trang_thai IN (
        'HOAT_DONG',   -- Lô đang dùng
        'HET_HANG',    -- Số lượng đã phát hết
        'HET_HAN',     -- Đã quá hạn sử dụng
        'DA_XUAT'      -- Đã xuất khỏi kho (hủy lô)
    )),

    FOREIGN KEY (ma_thuoc) REFERENCES thuoc(id),
    FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien(id)
);

-- Xuất kho: ghi nhận việc xuất/hủy lô thuốc hết hạn hoặc hư hỏng
CREATE TABLE xuat_kho_thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_lo_thuoc INT NOT NULL,            -- Lô thuốc bị xuất
    ma_nhan_vien INT NOT NULL,           -- Nhân viên kho thực hiện
    so_luong_xuat INT NOT NULL,

    -- Lý do xuất:
    -- HET_HAN   : lô hết hạn sử dụng
    -- HU_HONG   : thuốc hư hỏng, vỡ vạc
    -- KIEM_KE   : điều chỉnh sau kiểm kê thực tế
    ly_do VARCHAR(30) NOT NULL DEFAULT 'HET_HAN',
    CONSTRAINT chk_ly_do_xuat CHECK (ly_do IN ('HET_HAN', 'HU_HONG', 'KIEM_KE')),

    ghi_chu TEXT,
    ngay_xuat DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (ma_lo_thuoc) REFERENCES nhap_kho_thuoc(id),
    FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien(id)
);

-- ============================================================
-- PHẦN 2: BẢNG CHỨC NĂNG NGƯỜI DÙNG (PUBLIC FEATURES)
-- ============================================================

-- ------------------------------------------------------------
-- 1. TIN TỨC (News)
-- ------------------------------------------------------------
CREATE TABLE danh_muc_tin_tuc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ten_danh_muc VARCHAR(100) NOT NULL,
    slug VARCHAR(120) UNIQUE NOT NULL
);

CREATE TABLE tin_tuc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_danh_muc INT,
    tieu_de VARCHAR(300) NOT NULL,
    slug VARCHAR(320) UNIQUE NOT NULL,
    anh_dai_dien VARCHAR(500),
    noi_dung LONGTEXT,
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ma_danh_muc) REFERENCES danh_muc_tin_tuc(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 2. GIỚI THIỆU BỆNH VIỆN (About / Introduction)
-- ------------------------------------------------------------
CREATE TABLE gioi_thieu (
    id INT PRIMARY KEY DEFAULT 1,  -- Singleton, luôn là ID = 1
    tieu_de VARCHAR(200) NOT NULL,
    noi_dung LONGTEXT,
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- CÀI ĐẶT HỀ THỐNG (Site Settings)
--    Quản lý các thông số hiển thị toàn site: logo header, banner trang chủ
--    Singleton table (chỉ có 1 bản ghi duy nhất, ID = 1)
-- ------------------------------------------------------------
CREATE TABLE cai_dat_he_thong (
    id INT PRIMARY KEY DEFAULT 1,
    ten_benh_vien VARCHAR(200),         -- Tên hiển thị trên header
    duong_dan_logo VARCHAR(500),        -- Logo ở header (góc trên trái)
    duong_dan_banner VARCHAR(500),      -- Ảnh banner trang chủ (hero section)
    mau_chu_dao VARCHAR(20),            -- Mã màu theme (VD: '#0d6efd')
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 3. SƠ ĐỒ BỆNH VIỆN (Hospital Map)
-- ------------------------------------------------------------
CREATE TABLE so_do_benh_vien (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ten_tang VARCHAR(100) NOT NULL -- Nên để là tên tầng cho rõ ràng
);

CREATE TABLE vi_tri_phong (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_so_do INT NOT NULL,
    so_phong VARCHAR(20),
    ten_phong VARCHAR(100) NOT NULL,
    mo_ta TEXT,
    loai_phong VARCHAR(20), -- Ví dụ: 'CAP_CUU', 'TIEP_DON', 'NHA_THUOC'
    FOREIGN KEY (ma_so_do) REFERENCES so_do_benh_vien(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 4. KHOA / CHUYÊN KHOA (Departments)
-- ------------------------------------------------------------
CREATE TABLE khoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ten_khoa VARCHAR(150) NOT NULL,
    slug VARCHAR(170) UNIQUE NOT NULL,
    mo_ta_ngan VARCHAR(300),
    noi_dung_gioi_thieu LONGTEXT, -- Lưu nội dung giới thiệu
    trang_thiet_bi LONGTEXT,      -- Lưu danh sách máy móc (tách riêng để dễ render)
    thu_tu INT DEFAULT 0,
    trang_thai TINYINT DEFAULT 1,
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Liên kết bác sĩ ↔ khoa (nhiều-nhiều)
CREATE TABLE bac_si_khoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_nhan_vien INT NOT NULL,
    ma_khoa INT NOT NULL,
    chuc_vu VARCHAR(100) COMMENT 'VD: Trưởng khoa, Bác sĩ điều trị',
    UNIQUE KEY uq_bacsi_khoa (ma_nhan_vien, ma_khoa),
    FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien(id) ON DELETE CASCADE,
    FOREIGN KEY (ma_khoa) REFERENCES khoa(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 6. LIÊN HỆ (Contact)
-- ------------------------------------------------------------
CREATE TABLE thong_tin_lien_he (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dia_chi TEXT,
    so_dien_thoai VARCHAR(50),
    email VARCHAR(150),
    gio_lam_viec VARCHAR(255) COMMENT 'VD: Thứ 2 - Thứ 7: 7:00 - 17:00',
    link_ban_do VARCHAR(500) COMMENT 'Google Maps embed URL'
);

CREATE TABLE yeu_cau_lien_he (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    so_dien_thoai VARCHAR(20),
    chu_de VARCHAR(200),
    noi_dung TEXT NOT NULL,
    ngay_gui TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 7. CÂU HỎI THƯỜNG GẶP (FAQ)
-- ------------------------------------------------------------
CREATE TABLE danh_muc_faq (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ten_danh_muc VARCHAR(150) NOT NULL
);

CREATE TABLE cau_hoi_thuong_gap (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_danh_muc INT,
    cau_hoi VARCHAR(400) NOT NULL,
    tra_loi TEXT NOT NULL,
    FOREIGN KEY (ma_danh_muc) REFERENCES danh_muc_faq(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 8. HƯỚNG DẪN Sử DỤNG (User Guide)
--    Khác với FAQ: Hướng dẫn là bài viết dài có tiêu đề, được phân loại
--    (VD: "Cách đặt lịch online", "Cách lấy số thứ tự"...)
-- ------------------------------------------------------------
CREATE TABLE danh_muc_huong_dan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ten_danh_muc VARCHAR(150) NOT NULL,
    thu_tu INT DEFAULT 0
);

CREATE TABLE huong_dan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_danh_muc INT,
    tieu_de VARCHAR(300) NOT NULL,
    noi_dung LONGTEXT NOT NULL,
    thu_tu INT DEFAULT 0,           -- Thứ tự hiển thị trong danh mục
    trang_thai TINYINT DEFAULT 1,   -- 1: hiện, 0: ẩn
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ma_danh_muc) REFERENCES danh_muc_huong_dan(id) ON DELETE SET NULL
);

-- ============================================================
-- PHẦN 3: QUẢN LÝ HÀNG ĐỢI & THANH TOÁN TẠI PHÒNG KHÁM
-- ============================================================
-- Điều dưỡng tra cứu bệnh nhân (SĐT / mã lịch hẹn)
-- → Kiểm tra/thu thanh toán → Cấp STT vào đúng làn của bác sĩ
-- ============================================================

-- ------------------------------------------------------------
-- 1. THANH TOÁN LỊCH HẸN
-- ------------------------------------------------------------
CREATE TABLE thanh_toan_lich_hen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_lich_hen INT NOT NULL UNIQUE,
    so_tien DECIMAL(12,2) NOT NULL DEFAULT 0,

    -- 'ONLINE' (bệnh nhân đã trả trước khi đến) hoặc 'TAI_QUAY'
    phuong_thuc VARCHAR(30) NOT NULL DEFAULT 'TAI_QUAY',
    CONSTRAINT chk_phuong_thuc CHECK (phuong_thuc IN ('ONLINE', 'TAI_QUAY')),

    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_THANH_TOAN',
    CONSTRAINT chk_tt_thanh_toan CHECK (trang_thai IN ('CHO_THANH_TOAN', 'DA_THANH_TOAN')),

    -- Điều dưỡng thu tiền tại quầy (NULL nếu thanh toán online)
    ma_nhan_vien_thu INT,
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (ma_lich_hen) REFERENCES lich_hen(id) ON DELETE CASCADE,
    FOREIGN KEY (ma_nhan_vien_thu) REFERENCES nhan_vien(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 2. SỐ THỨ TỰ & HÀNG ĐỢI 2 LÀN
--    STT được đánh riêng theo từng bác sĩ trong mỗi ngày:
--    Phòng BS01: 1, 2, 3 ...
--    Phòng BS02: 1, 2, 3 ...
-- ------------------------------------------------------------
-- Mọi bệnh nhân đều phải có lịch hẹn trước khi được cấp STT.
-- Nếu chưa có lịch → điều dưỡng tạo lịch tại quầy (nguon_tao = 'TAI_QUAY')
-- rồi mới thu tiền và cấp STT.
CREATE TABLE so_thu_tu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_lich_hen INT NOT NULL,       -- Bắt buộc có lịch hẹn (online hoặc tại quầy)
    ma_benh_nhan INT NOT NULL,
    ma_bac_si INT NOT NULL,         -- Xác định "phòng" bác sĩ trong ca
    ngay_kham DATE NOT NULL,

    -- STT tăng dần theo từng bác sĩ mỗi ngày (Phòng 01: 1,2,3 | Phòng 02: 1,2,3)
    so_thu_tu INT NOT NULL,

    -- Làn 1 (KHAM_MOI): chưa có xét nghiệm, bác sĩ hỏi bệnh + viết chỉ định
    -- Làn 2 (KHAM_LAI): đã có kết quả, bác sĩ chỉ đọc + kê đơn → ưu tiên
    lan_kham VARCHAR(20) NOT NULL DEFAULT 'KHAM_MOI',
    CONSTRAINT chk_lan_kham CHECK (lan_kham IN ('KHAM_MOI', 'KHAM_LAI')),

    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_GOI',
    CONSTRAINT chk_trang_thai_stt CHECK (trang_thai IN (
        'CHO_GOI',    -- Đang chờ ngoài
        'DANG_KHAM',  -- Đang trong phòng bác sĩ
        'HOAN_THANH'  -- Khám xong
    )),

    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Mỗi bác sĩ chỉ có 1 STT cho 1 số trong 1 ngày
    UNIQUE KEY uq_stt_bacsi_ngay (ma_bac_si, ngay_kham, so_thu_tu),

    FOREIGN KEY (ma_lich_hen) REFERENCES lich_hen(id),
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(id),
    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id)
);

-- ------------------------------------------------------------
-- NOTE nghiệp vụ backend:
--
-- Sinh STT tiếp theo cho bác sĩ trong ngày:
--   SELECT COALESCE(MAX(so_thu_tu), 0) + 1 FROM so_thu_tu
--   WHERE ma_bac_si = ? AND ngay_kham = CURDATE()
--
-- Xác định lan_kham khi cấp STT:
--   Nếu bệnh nhân có yeu_cau_dich_vu.trang_thai = 'DA_CO_KET_QUA'
--   → lan_kham = 'KHAM_LAI', ngược lại → 'KHAM_MOI'
-- ------------------------------------------------------------


