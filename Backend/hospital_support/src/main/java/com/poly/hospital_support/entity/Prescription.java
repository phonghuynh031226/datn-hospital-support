    package com.poly.hospital_support.entity;

    import jakarta.persistence.*;
    import lombok.Data;

    @Entity
    @Table(name = "prescriptions")
    @Data
    public class Prescription {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        private Integer doctorId;

        private Integer patientId;

        private String note;

        private String status;
    }