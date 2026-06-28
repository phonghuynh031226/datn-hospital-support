package com.poly.hospital_support.repository;

import com.poly.hospital_support.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {
}
