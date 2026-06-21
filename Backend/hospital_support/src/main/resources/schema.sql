CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS tai_khoan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    so_dien_thoai VARCHAR(15),
    email VARCHAR(100),
    mat_khau VARCHAR(255) NOT NULL,
    vai_tro VARCHAR(30) NOT NULL,
    trang_thai TINYINT DEFAULT 1,
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_vai_tro CHECK (vai_tro IN ('BENH_NHAN', 'DIEU_DUONG', 'BAC_SI', 'DUOC_SI', 'THU_KHO'))
);

CREATE TABLE IF NOT EXISTS benh_nhan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_tai_khoan INT UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    gioi_tinh VARCHAR(10),
    ngay_sinh DATE,
    dia_chi TEXT,
    CONSTRAINT chk_gioi_tinh_bn CHECK (gioi_tinh IN ('Nam', 'Nu', 'Khac')),
    FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS nhan_vien (
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

CREATE TABLE IF NOT EXISTS bio_bacsi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_tai_khoan INT UNIQUE,
    noidung LONGTEXT,
    FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lich_hen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_benh_nhan INT NOT NULL,
    ma_bac_si INT NOT NULL,
    ngay_hen DATE NOT NULL,
    khung_gio VARCHAR(50),
    trieu_chung TEXT,
    trang_thai VARCHAR(30) DEFAULT 'CHO_XAC_NHAN',
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_trang_thai_lh CHECK (trang_thai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DA_HUY')),
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(id),
    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id)
);

CREATE TABLE IF NOT EXISTS benh_an (
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

CREATE TABLE IF NOT EXISTS yeu_cau_dich_vu (
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

CREATE TABLE IF NOT EXISTS thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ten_thuoc VARCHAR(150) NOT NULL,
    don_vi_tinh VARCHAR(20) NOT NULL,
    don_gia DECIMAL(12,2) DEFAULT 0,
    so_luong_ton INT DEFAULT 0,
    so_luong_toi_thieu INT DEFAULT 10,
    han_su_dung DATE
);

CREATE TABLE IF NOT EXISTS don_thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_benh_an INT NOT NULL UNIQUE,
    ma_bac_si INT NOT NULL,
    ma_benh_nhan INT NOT NULL,
    trang_thai VARCHAR(30) DEFAULT 'CHO_PHAT_THUOC',
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_trang_thai_dt CHECK (trang_thai IN ('CHO_PHAT_THUOC', 'DA_PHAT_THUOC')),
    FOREIGN KEY (ma_benh_an) REFERENCES benh_an(id) ON DELETE CASCADE,
    FOREIGN KEY (ma_bac_si) REFERENCES nhan_vien(id),
    FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(id)
);

CREATE TABLE IF NOT EXISTS chi_tiet_don_thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_don_thuoc INT NOT NULL,
    ma_thuoc INT NOT NULL,
    so_luong INT NOT NULL,
    huong_dan_su_dung VARCHAR(255),
    FOREIGN KEY (ma_don_thuoc) REFERENCES don_thuoc(id) ON DELETE CASCADE,
    FOREIGN KEY (ma_thuoc) REFERENCES thuoc(id)
);

CREATE TABLE IF NOT EXISTS nhap_kho_thuoc (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_thuoc INT NOT NULL,
    so_luong INT NOT NULL,
    don_gia_nhap DECIMAL(12,2),
    ngay_nhap DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ma_thuoc) REFERENCES thuoc(id)
);
