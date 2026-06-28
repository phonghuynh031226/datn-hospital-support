package com.poly.hospital_support.impl;

import com.poly.hospital_support.repository.AppointmentRepository;
import com.poly.hospital_support.repository.BenhNhanRepository;
import com.poly.hospital_support.repository.ExaminationQueueRepository;
import com.poly.hospital_support.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ExaminationQueueRepository examinationQueueRepository;

    @Override
    public Object getPatients() {
        return benhNhanRepository.findAll();
    }

    @Override
    public Object getAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Object getWaitingPatients() {
        return examinationQueueRepository.findAll();
    }

}