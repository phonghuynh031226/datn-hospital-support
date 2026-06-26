package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "danh_muc_thuoc") // Tên bảng danh mục thuốc trong DB
public class MedicineCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_danh_muc", nullable = false, length = 100)
    private String name;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;

    // Quan hệ Một-Nhiều: Một danh mục có thể chứa nhiều thuốc
    // mappedBy phải khớp với tên biến 'category' sẽ khai báo trong lớp Medicine
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Medicine> medicines;

    // --- Getters and Setters ---

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
    }
}