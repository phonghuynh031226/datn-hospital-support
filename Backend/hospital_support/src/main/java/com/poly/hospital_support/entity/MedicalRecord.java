package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "benh_an") // Khớp với tên bảng 'benh_an' trong SQL
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ 1-1 với lịch hẹn (ma_lich_hen)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lich_hen", unique = true)
    private Appointment appointment;

    // Quan hệ Nhiều-Một với bệnh nhân (ma_benh_nhan)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_benh_nhan", nullable = false)
    private PatientProfile patient; // Sử dụng PatientProfile theo thư mục entity của bạn

    // Quan hệ Nhiều-Một với bác sĩ (ma_bac_si)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_bac_si", nullable = false)
    private StaffProfile doctor; // Sử dụng StaffProfile theo thư mục entity của bạn

    @Column(name = "trieu_chung", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "chan_doan", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "ket_luan", columnDefinition = "TEXT")
    private String conclusion;

    @Column(name = "trang_thai", length = 30)
    private String status = "CHO_KHAM"; // Mặc định theo SQL: CHO_KHAM, DANG_KHAM, CHO_XET_NGHIEM, CHO_KE_DON, HOAN_THANH

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public PatientProfile getPatient() {
        return patient;
    }

    public void setPatient(PatientProfile patient) {
        this.patient = patient;
    }

    public StaffProfile getDoctor() {
        return doctor;
    }

    public void setDoctor(StaffProfile doctor) {
        this.doctor = doctor;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}