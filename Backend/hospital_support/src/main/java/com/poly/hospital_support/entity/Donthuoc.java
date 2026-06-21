package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "don_thuoc")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Donthuoc extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "ma_benh_an")
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "ma_bac_si")
    private StaffProfile doctor;

    @ManyToOne
    @JoinColumn(name = "ma_benh_nhan")
    private PatientProfile patient;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    // Liên kết 1-nhiều với chi tiết đơn thuốc
    @OneToMany(mappedBy = "donThuoc", cascade = CascadeType.ALL)
    private List<CtDonthuoc> chiTietDonThuocs;
}