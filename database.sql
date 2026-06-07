-- ============================================================
-- HỆ THỐNG QUẢN LÝ BỆNH VIỆN - DATABASE HOÀN CHỈNH
-- Phiên bản: 3.0
-- Mô tả: Schema đầy đủ cho tất cả các vai trò:
--   Lai (Điều dưỡng), Thịnh (Giám đốc), Nhựt (Kho),
--   Quân (Dược sĩ), Phong (Bệnh nhân), Tỷ (Bác sĩ)
-- ============================================================

CREATE DATABASE IF NOT EXISTS hospital_management_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hospital_management_system;

-- ============================================================
-- I. PHÂN HỆ HỆ THỐNG DÙNG CHUNG & PHÂN QUYỀN
-- ============================================================

-- 1. Bảng Vai trò
CREATE TABLE roles (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(20)  NOT NULL UNIQUE,
    description VARCHAR(250)
);

-- 2. Bảng Tài khoản hệ thống
CREATE TABLE accounts (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,          -- bcrypt hash
    role_id    INT          NOT NULL,
    status     TINYINT      DEFAULT 1,         -- 1: Hoạt động, 0: Khóa
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 3. Bảng Token đặt lại mật khẩu (cho Phong: quên mật khẩu)
CREATE TABLE password_reset_tokens (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT          NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME     NOT NULL,
    used       TINYINT(1)   DEFAULT 0,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- 3b. Bảng Blacklist JWT (vô hiệu hóa token khi logout / đổi mật khẩu)
--     Lưu jti (JWT ID) thay vì toàn bộ token string → nhẹ hơn nhiều
--     Backend kiểm tra: nếu jti tồn tại trong bảng này → từ chối request
--     Chạy cronjob định kỳ xóa bản ghi đã hết hạn để giữ bảng gọn
CREATE TABLE jwt_blacklist (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    jti        VARCHAR(100) NOT NULL UNIQUE,   -- JWT ID (uuid trong payload)
    account_id INT          NOT NULL,
    expires_at DATETIME     NOT NULL,          -- Thời điểm token hết hạn gốc
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- 4. Bảng Khoa bệnh viện
CREATE TABLE departments (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    description     TEXT
);

-- 5. Bảng Phòng chức năng
CREATE TABLE rooms (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20)  NOT NULL UNIQUE,
    room_type   VARCHAR(50)  NOT NULL,   -- CLINIC, PHARMACY, WAREHOUSE
    department_id INT NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 6. Bảng Cấu hình hệ thống  [THÊM MỚI - cho Thịnh: cấu hình hệ thống]
CREATE TABLE system_configs (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    config_key   VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description  VARCHAR(255),
    updated_by   INT NULL,               -- staff_profiles.id (set sau)
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. Bảng Thông tin nhân viên nội bộ
CREATE TABLE staff_profiles (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    account_id    INT          NOT NULL UNIQUE,
    full_name     VARCHAR(100) NOT NULL,
    gender        VARCHAR(10),
    phone         VARCHAR(15),
    email         VARCHAR(100),
    department_id INT NULL,
    room_id       INT NULL,
    -- [THÊM MỚI - cho Phong: xem thông tin đội ngũ y tế]
    specialty     VARCHAR(100) NULL,       -- Chuyên khoa (dùng cho bác sĩ)
    bio           TEXT         NULL,       -- Tiểu sử giới thiệu
    avatar_url    VARCHAR(255) NULL,       -- Ảnh đại diện
    is_public     TINYINT(1)   DEFAULT 0, -- 1: Hiện ra trang public
    FOREIGN KEY (account_id)    REFERENCES accounts(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (room_id)       REFERENCES rooms(id)
);

-- Thêm FK cho system_configs.updated_by sau khi có staff_profiles
ALTER TABLE system_configs
    ADD CONSTRAINT fk_syscfg_staff FOREIGN KEY (updated_by) REFERENCES staff_profiles(id);

-- 8. Bảng Thông báo nội bộ  [THÊM MỚI - cho Quân: nhận thông báo đơn thuốc từ bác sĩ]
CREATE TABLE notifications (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    account_id   INT          NOT NULL,  -- Người nhận thông báo
    type         VARCHAR(50)  NOT NULL,  -- NEW_PRESCRIPTION, APPOINTMENT_CONFIRMED, ...
    reference_id INT          NULL,      -- ID bản ghi liên quan (prescription, appointment...)
    message      VARCHAR(255) NOT NULL,
    is_read      TINYINT(1)   DEFAULT 0,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- ============================================================
-- II. PHÂN HỆ BỆNH NHÂN & ĐẶT LỊCH KHÁM
-- ============================================================

-- 9. Bảng Thông tin bệnh nhân
CREATE TABLE patient_profiles (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT  NULL UNIQUE,    -- NULL nếu đăng ký tại chỗ bởi Điều dưỡng
    full_name  VARCHAR(100) NOT NULL,
    gender     VARCHAR(10),
    dob        DATE,
    phone      VARCHAR(15) NOT NULL,
    address    VARCHAR(255),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- 10. Bảng Lịch làm việc của Bác sĩ
CREATE TABLE doctor_schedules (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id    INT         NOT NULL,
    work_date    DATE        NOT NULL,
    shift_type   VARCHAR(20) NOT NULL,  -- MORNING, AFTERNOON
    max_patients INT         DEFAULT 30,
    FOREIGN KEY (doctor_id) REFERENCES staff_profiles(id),
    UNIQUE KEY uq_doctor_schedule (doctor_id, work_date, shift_type)
);

-- 11. Bảng Đặt lịch khám (Online hoặc Tại chỗ)
CREATE TABLE appointments (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    patient_id       INT         NOT NULL,
    schedule_id      INT         NOT NULL,
    appointment_type VARCHAR(20) NOT NULL,             -- ONLINE, WALK_IN
    appointment_date DATE        NOT NULL,
    status           VARCHAR(30) DEFAULT 'PENDING',    -- PENDING, CONFIRMED, CANCELLED
    note             VARCHAR(255) NULL,                -- Ghi chú triệu chứng ban đầu
    created_by       INT         NULL,                 -- NULL = bệnh nhân tự đặt, có giá trị = Điều dưỡng đặt hộ
    created_at       DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)  REFERENCES patient_profiles(id),
    FOREIGN KEY (schedule_id) REFERENCES doctor_schedules(id),
    FOREIGN KEY (created_by)  REFERENCES staff_profiles(id)
);

-- 12. Bảng Hàng đợi khám bệnh tại phòng khám
CREATE TABLE examination_queues (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NULL,
    patient_id     INT NOT NULL,
    room_id        INT NOT NULL,
    ticket_number  INT NOT NULL,
    status         VARCHAR(30) DEFAULT 'WAITING',  -- WAITING, IN_PROGRESS, COMPLETED, SKIPPED
    created_at     DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (patient_id)     REFERENCES patient_profiles(id),
    FOREIGN KEY (room_id)        REFERENCES rooms(id)
);

-- ============================================================
-- III. PHÂN HỆ KHÁM BỆNH & ĐƠN THUỐC
-- ============================================================

-- 13. Bảng Hồ sơ bệnh án (Kết quả khám)
CREATE TABLE medical_records (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT  NOT NULL,
    doctor_id      INT  NOT NULL,
    room_id        INT  NOT NULL,
    appointment_id INT  NULL,    -- [THÊM MỚI] Liên kết lại lịch hẹn gốc
    symptoms       TEXT,
    diagnosis      TEXT,
    conclusion     TEXT,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)     REFERENCES patient_profiles(id),
    FOREIGN KEY (doctor_id)      REFERENCES staff_profiles(id),
    FOREIGN KEY (room_id)        REFERENCES rooms(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- 14. Bảng Đơn thuốc bác sĩ kê
CREATE TABLE prescriptions (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id INT         NOT NULL UNIQUE,
    doctor_id         INT         NOT NULL,
    patient_id        INT         NOT NULL,
    status            VARCHAR(30) DEFAULT 'PENDING',  -- PENDING, DISPENSED
    note              VARCHAR(255) NULL,
    created_at        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id),
    FOREIGN KEY (doctor_id)         REFERENCES staff_profiles(id),
    FOREIGN KEY (patient_id)        REFERENCES patient_profiles(id)
);

-- ============================================================
-- IV. PHÂN HỆ QUẢN LÝ KHO THUỐC & DANH MỤC THUỐC
-- ============================================================

-- 15. Bảng Nhóm/Loại thuốc
CREATE TABLE medicine_categories (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

-- 16. Bảng Danh mục Thuốc
CREATE TABLE medicines (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    category_id      INT            NOT NULL,
    medicine_code    VARCHAR(50)    NOT NULL UNIQUE,
    medicine_name    VARCHAR(150)   NOT NULL,
    active_ingredient VARCHAR(150),
    unit             VARCHAR(20)    NOT NULL,   -- Vien, Chai, Vi
    selling_price    DECIMAL(10,2)  NOT NULL,
    description      TEXT           NULL,
    status           TINYINT(1)     DEFAULT 1,  -- 1: Đang dùng, 0: Ngừng kinh doanh
    FOREIGN KEY (category_id) REFERENCES medicine_categories(id)
);

-- 17. Bảng Chi tiết các loại thuốc trong đơn
CREATE TABLE prescription_items (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id      INT          NOT NULL,
    medicine_id          INT          NOT NULL,
    quantity             INT          NOT NULL,
    dosage_instruction   VARCHAR(255),          -- Hướng dẫn dùng thuốc
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    FOREIGN KEY (medicine_id)     REFERENCES medicines(id)
);

-- 18. Bảng Lô thuốc nhập kho
CREATE TABLE medicine_batches (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id      INT           NOT NULL,
    batch_code       VARCHAR(50)   NOT NULL,
    import_price     DECIMAL(10,2) NOT NULL,
    initial_quantity INT           NOT NULL,
    current_quantity INT           NOT NULL,
    manufacture_date DATE,
    expiry_date      DATE          NOT NULL,   -- Hạn sử dụng
    supplier         VARCHAR(150)  NULL,       -- Nhà cung cấp
    created_at       DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id),
    UNIQUE KEY uq_batch (medicine_id, batch_code)
);

-- 19. Bảng Nhật ký Nhập/Xuất kho thuốc
CREATE TABLE inventory_logs (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    log_type   VARCHAR(20) NOT NULL,               -- IMPORT, EXPORT
    staff_id   INT         NOT NULL,
    note       VARCHAR(255),
    created_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff_profiles(id)
);

-- 20. Bảng Chi tiết phiếu nhập/xuất kho
CREATE TABLE inventory_log_items (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    log_id   INT NOT NULL,
    batch_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (log_id)   REFERENCES inventory_logs(id),
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(id)
);

-- ============================================================
-- V. PHÂN HỆ HÓA ĐƠN & THANH TOÁN
-- ============================================================

-- 21. Bảng Hóa đơn thanh toán
CREATE TABLE invoices (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    patient_id      INT            NOT NULL,
    staff_id        INT            NOT NULL,   -- Dược sĩ hoặc thu ngân lập
    appointment_id  INT            NULL,
    prescription_id INT            NULL,
    total_amount    DECIMAL(10,2)  NOT NULL,
    payment_method  VARCHAR(20)    NOT NULL,   -- CASH, VNPAY, MOMO
    status          VARCHAR(30)    DEFAULT 'UNPAID',  -- UNPAID, PAID, REFUNDED
    paid_at         DATETIME       NULL,
    created_at      DATETIME       DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)      REFERENCES patient_profiles(id),
    FOREIGN KEY (staff_id)        REFERENCES staff_profiles(id),
    FOREIGN KEY (appointment_id)  REFERENCES appointments(id),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id)
);

-- 22. Bảng Chi tiết hóa đơn (các dòng mục trong hóa đơn)
CREATE TABLE invoice_items (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id  INT           NOT NULL,
    item_type   VARCHAR(30)   NOT NULL,    -- MEDICINE, EXAMINATION_FEE, OTHER
    description VARCHAR(255)  NOT NULL,
    quantity    INT           DEFAULT 1,
    unit_price  DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ============================================================
-- VI. PHÂN HỆ TRUYỀN THÔNG & LIÊN HỆ HỖ TRỢ
-- ============================================================

-- 23. Bảng Bài viết / Tin tức bệnh viện
CREATE TABLE news (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    thumbnail  VARCHAR(255),
    created_by INT          NOT NULL,
    status     VARCHAR(20)  DEFAULT 'DRAFT',  -- DRAFT, PUBLISHED
    published_at DATETIME   NULL,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES staff_profiles(id)
);

-- 24. Bảng Ý kiến hỏi đáp / Liên hệ từ bệnh nhân
CREATE TABLE contacts (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    phone_email VARCHAR(100) NOT NULL,
    title       VARCHAR(150) NOT NULL,
    message     TEXT         NOT NULL,
    reply       TEXT         NULL,        -- Nội dung phản hồi
    status      VARCHAR(20)  DEFAULT 'PENDING',  -- PENDING, RESOLVED
    replied_by  INT          NULL,
    replied_at  DATETIME     NULL,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (replied_by) REFERENCES staff_profiles(id)
);

-- ============================================================
-- VII. DỮ LIỆU MẪU CƠ BẢN (SEED DATA)
-- ============================================================

-- Vai trò
INSERT INTO roles (id, role_name, description) VALUES
(1, 'DIRECTOR',    'Giám đốc bệnh viện - Quản trị hệ thống và xem báo cáo'),
(2, 'NURSE',       'Điều dưỡng hành chính - Tiếp đón, điều phối bệnh nhân'),
(3, 'DOCTOR',      'Bác sĩ chuyên khoa - Khám bệnh và kê đơn thuốc'),
(4, 'PHARMACIST',  'Dược sĩ quầy thuốc - Phát thuốc, thu tiền hóa đơn'),
(5, 'WAREHOUSE',   'Nhân viên kho thuốc - Quản lý lô thuốc xuất nhập'),
(6, 'PATIENT',     'Bệnh nhân - Người dùng đăng ký trực tuyến');

-- Cấu hình hệ thống mặc định
INSERT INTO system_configs (config_key, config_value, description) VALUES
('HOSPITAL_NAME',       'Bệnh Viện Đa Khoa ABC',        'Tên bệnh viện hiển thị'),
('HOSPITAL_ADDRESS',    '123 Đường Nguyễn Huệ, Q.1, TP.HCM', 'Địa chỉ bệnh viện'),
('HOSPITAL_PHONE',      '028 1234 5678',                 'Hotline liên hệ'),
('HOSPITAL_EMAIL',      'contact@benhvienabc.vn',        'Email liên hệ chính'),
('HOSPITAL_LOGO_URL',   '/uploads/logo.png',             'URL logo bệnh viện'),
('WORKING_HOURS',       '07:00 - 17:00',                 'Giờ làm việc'),
('MAX_ONLINE_APPOINTMENTS_PER_SLOT', '30',               'Số lượt đặt lịch tối đa mỗi ca'),
('APPOINTMENT_REMINDER_HOURS', '24',                     'Số giờ nhắc lịch hẹn trước');

-- Khoa bệnh viện mẫu
INSERT INTO departments (id, department_name, description) VALUES
(1, 'Khoa Nội Tổng Hợp',  'Khám và điều trị bệnh nội khoa'),
(2, 'Khoa Ngoại Tổng Hợp','Khám và điều trị bệnh ngoại khoa'),
(3, 'Khoa Nhi',           'Khám và điều trị bệnh nhi'),
(4, 'Khoa Tim Mạch',      'Khám và điều trị bệnh tim mạch'),
(5, 'Khoa Da Liễu',       'Khám và điều trị bệnh da liễu'),
(6, 'Khoa Dược',          'Bộ phận dược và cấp phát thuốc'),
(7, 'Kho Dược',           'Kho lưu trữ và quản lý thuốc');

-- Phòng chức năng mẫu
INSERT INTO rooms (room_number, room_type, department_id) VALUES
('P101', 'CLINIC',    1),
('P102', 'CLINIC',    1),
('P201', 'CLINIC',    2),
('P301', 'CLINIC',    3),
('P401', 'CLINIC',    4),
('P501', 'CLINIC',    5),
('PH01', 'PHARMACY',  6),
('KH01', 'WAREHOUSE', 7);

-- Nhóm thuốc mẫu
INSERT INTO medicine_categories (category_name) VALUES
('Kháng sinh'),
('Hạ sốt - Giảm đau'),
('Tim mạch - Huyết áp'),
('Tiêu hóa'),
('Hô hấp'),
('Da liễu'),
('Vitamin & Khoáng chất'),
('Thuốc tiêu đường');

-- ============================================================
-- VIII. VIEWS HỖ TRỢ BÁO CÁO
-- ============================================================

-- View: Danh sách bệnh nhân đặt lịch hôm nay (cho Lai - Điều dưỡng)
CREATE OR REPLACE VIEW v_today_appointments AS
SELECT
    a.id                 AS appointment_id,
    a.appointment_type,
    a.appointment_date,
    a.status,
    pp.full_name         AS patient_name,
    pp.phone             AS patient_phone,
    pp.gender,
    pp.dob,
    sp.full_name         AS doctor_name,
    d.department_name,
    ds.shift_type,
    eq.ticket_number,
    eq.status            AS queue_status
FROM appointments a
JOIN patient_profiles pp   ON a.patient_id   = pp.id
JOIN doctor_schedules ds   ON a.schedule_id  = ds.id
JOIN staff_profiles sp     ON ds.doctor_id   = sp.id
LEFT JOIN departments d    ON sp.department_id = d.id
LEFT JOIN examination_queues eq ON a.id = eq.appointment_id
WHERE a.appointment_date = CURDATE();

-- View: Đơn thuốc chờ phát (cho Quân - Dược sĩ)
CREATE OR REPLACE VIEW v_pending_prescriptions AS
SELECT
    p.id                 AS prescription_id,
    p.status,
    p.created_at,
    pp.full_name         AS patient_name,
    pp.phone,
    sp.full_name         AS doctor_name,
    mr.diagnosis,
    mr.conclusion
FROM prescriptions p
JOIN patient_profiles pp ON p.patient_id = pp.id
JOIN staff_profiles sp   ON p.doctor_id  = sp.id
JOIN medical_records mr  ON p.medical_record_id = mr.id
WHERE p.status = 'PENDING'
ORDER BY p.created_at ASC;

-- View: Lô thuốc sắp hết hạn trong 90 ngày (cho Nhựt - Kho)
CREATE OR REPLACE VIEW v_expiring_batches AS
SELECT
    mb.id             AS batch_id,
    mb.batch_code,
    mb.expiry_date,
    DATEDIFF(mb.expiry_date, CURDATE()) AS days_until_expiry,
    mb.current_quantity,
    mb.import_price,
    m.medicine_code,
    m.medicine_name,
    m.unit,
    mc.category_name
FROM medicine_batches mb
JOIN medicines m           ON mb.medicine_id = m.id
JOIN medicine_categories mc ON m.category_id = mc.id
WHERE mb.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 90 DAY)
  AND mb.current_quantity > 0
ORDER BY mb.expiry_date ASC;

-- View: Báo cáo doanh thu theo ngày (cho Thịnh - Giám đốc)
CREATE OR REPLACE VIEW v_revenue_by_date AS
SELECT
    DATE(created_at)          AS revenue_date,
    COUNT(*)                  AS total_invoices,
    SUM(total_amount)         AS total_revenue,
    SUM(CASE WHEN status = 'PAID'     THEN total_amount ELSE 0 END) AS paid_revenue,
    SUM(CASE WHEN status = 'UNPAID'   THEN total_amount ELSE 0 END) AS unpaid_revenue,
    SUM(CASE WHEN status = 'REFUNDED' THEN total_amount ELSE 0 END) AS refunded_revenue,
    SUM(CASE WHEN payment_method = 'CASH'   THEN total_amount ELSE 0 END) AS cash_revenue,
    SUM(CASE WHEN payment_method = 'VNPAY'  THEN total_amount ELSE 0 END) AS vnpay_revenue,
    SUM(CASE WHEN payment_method = 'MOMO'   THEN total_amount ELSE 0 END) AS momo_revenue
FROM invoices
GROUP BY DATE(created_at)
ORDER BY revenue_date DESC;

-- View: Lịch sử khám đầy đủ của bệnh nhân (cho Phong - Bệnh nhân & Tỷ - Bác sĩ)
CREATE OR REPLACE VIEW v_patient_medical_history AS
SELECT
    mr.id                AS record_id,
    mr.created_at        AS exam_date,
    pp.id                AS patient_id,
    pp.full_name         AS patient_name,
    pp.dob,
    pp.gender,
    sp.full_name         AS doctor_name,
    sp.specialty,
    d.department_name,
    r.room_number,
    mr.symptoms,
    mr.diagnosis,
    mr.conclusion,
    pres.id              AS prescription_id,
    pres.status          AS prescription_status
FROM medical_records mr
JOIN patient_profiles pp  ON mr.patient_id  = pp.id
JOIN staff_profiles sp    ON mr.doctor_id   = sp.id
LEFT JOIN departments d   ON sp.department_id = d.id
LEFT JOIN rooms r         ON mr.room_id     = r.id
LEFT JOIN prescriptions pres ON mr.id = pres.medical_record_id
ORDER BY mr.created_at DESC;

-- View: Thống kê lượt khám theo bác sĩ (cho Thịnh - Giám đốc)
CREATE OR REPLACE VIEW v_doctor_stats AS
SELECT
    sp.id                AS doctor_id,
    sp.full_name         AS doctor_name,
    sp.specialty,
    d.department_name,
    COUNT(mr.id)         AS total_examinations,
    COUNT(DISTINCT mr.patient_id) AS unique_patients,
    COUNT(pres.id)       AS total_prescriptions
FROM staff_profiles sp
JOIN accounts acc ON sp.account_id = acc.id
JOIN roles r_table ON acc.role_id = r_table.id AND r_table.role_name = 'DOCTOR'
LEFT JOIN departments d      ON sp.department_id = d.id
LEFT JOIN medical_records mr ON sp.id = mr.doctor_id
LEFT JOIN prescriptions pres ON mr.id = pres.medical_record_id
GROUP BY sp.id, sp.full_name, sp.specialty, d.department_name;

-- ============================================================
-- IX. INDEXES TỐI ƯU HIỆU SUẤT TRUY VẤN
-- ============================================================

-- Tìm kiếm theo ngày đặt lịch (thường xuyên dùng)
CREATE INDEX idx_appointments_date        ON appointments(appointment_date);
CREATE INDEX idx_appointments_patient     ON appointments(patient_id);
CREATE INDEX idx_appointments_status      ON appointments(status);

-- Hàng đợi khám
CREATE INDEX idx_queue_room_status        ON examination_queues(room_id, status);
CREATE INDEX idx_queue_patient            ON examination_queues(patient_id);

-- Hồ sơ bệnh án
CREATE INDEX idx_medical_records_patient  ON medical_records(patient_id);
CREATE INDEX idx_medical_records_doctor   ON medical_records(doctor_id);
CREATE INDEX idx_medical_records_date     ON medical_records(created_at);

-- Đơn thuốc
CREATE INDEX idx_prescriptions_patient    ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_status     ON prescriptions(status);

-- Lô thuốc hết hạn
CREATE INDEX idx_batches_expiry           ON medicine_batches(expiry_date);
CREATE INDEX idx_batches_medicine         ON medicine_batches(medicine_id);
CREATE INDEX idx_batches_qty              ON medicine_batches(current_quantity);

-- Hóa đơn
CREATE INDEX idx_invoices_patient         ON invoices(patient_id);
CREATE INDEX idx_invoices_status          ON invoices(status);
CREATE INDEX idx_invoices_date            ON invoices(created_at);

-- Thông báo
CREATE INDEX idx_notifications_account    ON notifications(account_id, is_read);

-- Lịch làm việc bác sĩ
CREATE INDEX idx_schedules_date           ON doctor_schedules(work_date);
CREATE INDEX idx_schedules_doctor         ON doctor_schedules(doctor_id);

-- Bài viết
CREATE INDEX idx_news_status              ON news(status, published_at);

-- JWT Blacklist
CREATE INDEX idx_jwt_blacklist_jti        ON jwt_blacklist(jti);
CREATE INDEX idx_jwt_blacklist_expires    ON jwt_blacklist(expires_at);