package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Singleton table - luôn có ID = 1
 */
@Entity
@Table(name = "gioi_thieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioiThieu {

    @Id
    @Column(name = "id", columnDefinition = "INT DEFAULT 1")
    private Integer id;

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "LONGTEXT")
    private String noiDung;

    @Column(name = "ngay_cap_nhat", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime ngayCapNhat;
}
