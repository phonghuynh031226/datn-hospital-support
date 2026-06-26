package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "thuoc") // Khớp với tên bảng 'thuoc' trong SQL
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_thuoc", nullable = false, length = 150)
    private String name;

    @Column(name = "don_vi_tinh", nullable = false, length = 20)
    private String unit;

    @Column(name = "don_gia", precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "so_luong_ton")
    private Integer inventoryQuantity = 0;

    @Column(name = "so_luong_toi_thieu")
    private Integer minimumQuantity = 10;

    @Column(name = "han_su_dung")
    private LocalDate expiryDate;

    // ĐÃ ĐƯA VÀO ĐÚNG VỊ TRÍ: Liên kết Nhiều-Một với bảng danh mục thuốc
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_danh_muc") // Tên cột khóa ngoại trong bảng thuoc
    private MedicineCategory category;

    // --- Getter và Setter ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(Integer inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    // ĐẦY ĐỦ Getter và Setter cho thuộc tính category
    public MedicineCategory getCategory() {
        return category;
    }

    public void setCategory(MedicineCategory category) {
        this.category = category;
    }
}