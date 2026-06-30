package com.poly.hospital_support.service;

import com.poly.hospital_support.dto.LichHenRequest;
import com.poly.hospital_support.dto.LichHenResponse;
import java.util.List;

public interface LichHenService {
    LichHenResponse createAppointment(LichHenRequest request);
    List<LichHenResponse> getAppointmentsByPatientId(Integer patientId);
    LichHenResponse cancelAppointment(Integer id);
    LichHenResponse confirmPayment(Integer id);
}
