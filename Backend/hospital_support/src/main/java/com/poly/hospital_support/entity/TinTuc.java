package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tin_tuc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TinTuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_danh_muc")
    private DanhMucTinTuc danhMuc;

    @Column(name = "tieu_de", nullable = false, length = 300)
    private String tieuDe;

    @Column(name = "slug", unique = true, nullable = false, length = 320)
    private String slug;

    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien;

    @Column(name = "noi_dung", columnDefinition = "LONGTEXT")
    private String noiDung;

    @Column(name = "ngay_tao", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayTao;
}
