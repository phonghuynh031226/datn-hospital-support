package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thong_tin_lien_he")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongTinLienHe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dia_chi", columnDefinition = "TEXT")
    private String diaChi;

    @Column(name = "so_dien_thoai", length = 50)
    private String soDienThoai;

    @Column(name = "email", length = 150)
    private String email;

    /** VD: Thứ 2 - Thứ 7: 7:00 - 17:00 */
    @Column(name = "gio_lam_viec", length = 255)
    private String gioLamViec;

    /** Google Maps embed URL */
    @Column(name = "link_ban_do", length = 500)
    private String linkBanDo;
}
