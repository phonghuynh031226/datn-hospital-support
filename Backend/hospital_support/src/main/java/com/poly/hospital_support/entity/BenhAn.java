package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "benh_an")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenhAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Liên kết với LichHen (nếu cần)
  //  @OneToOne
   // @JoinColumn(name = "ma_lich_hen")
  //  private LichHen lichHen;

    @ManyToOne
    @JoinColumn(name = "ma_benh_nhan")
    private BenhNhan benhNhan;

    @ManyToOne
    @JoinColumn(name = "ma_bac_si")
    private NhanVien nhanVien;

    private String trieuChung;
    private String chanDoan;
    private String ketLuan;
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @OneToOne(mappedBy = "benhAn", cascade = CascadeType.ALL)
    private DonThuoc donThuoc;
}