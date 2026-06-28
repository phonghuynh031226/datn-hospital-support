package com.poly.hospital_support.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "examination_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ExaminationQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;

    private Integer queueNumber;

    private String status;
}
