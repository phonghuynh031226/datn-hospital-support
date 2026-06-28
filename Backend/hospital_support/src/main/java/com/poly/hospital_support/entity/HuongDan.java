package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "huong_dan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuongDan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_danh_muc")
    private DanhMucHuongDan danhMuc;

    @Column(name = "tieu_de", nullable = false, length = 300)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "LONGTEXT")
    private String noiDung;

    /** Thứ tự hiển thị trong danh mục */
    @Column(name = "thu_tu", columnDefinition = "INT DEFAULT 0")
    private Integer thuTu;

    /** 1: hiện, 0: ẩn */
    @Column(name = "trang_thai", columnDefinition = "TINYINT DEFAULT 1")
    private Byte trangThai;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
