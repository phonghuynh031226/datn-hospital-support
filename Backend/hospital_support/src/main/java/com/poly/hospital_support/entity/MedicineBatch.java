package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhap_kho_thuoc") // Khớp với tên bảng 'nhap_kho_thuoc' trong SQL
public class MedicineBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Thiết lập mối quan hệ Nhiều-Một với bảng thuoc qua khóa ngoại ma_thuoc
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_thuoc", nullable = false)
    private Medicine medicine;

    @Column(name = "so_luong", nullable = false)
    private Integer quantity;

    @Column(name = "don_gia_nhap", precision = 12, scale = 2)
    private BigDecimal importPrice;

    @Column(name = "ngay_nhap")
    private LocalDateTime importDate = LocalDateTime.now(); // Tự động lấy thời gian hiện tại lúc tạo

    // --- Getter và Setter ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }
}