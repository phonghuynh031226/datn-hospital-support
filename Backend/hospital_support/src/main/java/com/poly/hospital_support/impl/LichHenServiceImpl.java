package com.poly.hospital_support.impl;

import com.poly.hospital_support.dto.LichHenRequest;
import com.poly.hospital_support.dto.LichHenResponse;
import com.poly.hospital_support.entity.BenhNhan;
import com.poly.hospital_support.entity.LichHen;
import com.poly.hospital_support.entity.NhanVien;
import com.poly.hospital_support.repository.BenhNhanRepository;
import com.poly.hospital_support.repository.LichHenRepository;
import com.poly.hospital_support.repository.NhanVienRepository;
import com.poly.hospital_support.service.LichHenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LichHenServiceImpl implements LichHenService {

    private final LichHenRepository lichHenRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;

    private LichHenResponse mapToResponse(LichHen lh) {
        return LichHenResponse.builder()
                .id(lh.getId())
                .patientId(lh.getBenhNhan().getId())
                .patientName(lh.getBenhNhan().getHoTen())
                .doctorId(lh.getBacSi().getId())
                .doctorName(lh.getBacSi().getHoTen())
                .ngayHen(lh.getNgayHen())
                .khungGio(lh.getKhungGio())
                .trieuChung(lh.getTrieuChung())
                .trangThai(lh.getTrangThai())
                .nguonTao(lh.getNguonTao())
                .ngayTao(lh.getNgayTao())
                .build();
    }

    @Override
    @Transactional
    public LichHenResponse createAppointment(LichHenRequest request) {
        BenhNhan bn = benhNhanRepository.findById(request.getPatientId())
                .orElseGet(() -> benhNhanRepository.findByTaiKhoanId(request.getPatientId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bệnh nhân!")));

        NhanVien nv = nhanVienRepository.findById(request.getDoctorId())
                .orElseGet(() -> nhanVienRepository.findByTaiKhoanId(request.getDoctorId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bác sĩ!")));

        LichHen lh = LichHen.builder()
                .benhNhan(bn)
                .bacSi(nv)
                .ngayHen(request.getNgayHen())
                .khungGio(request.getKhungGio())
                .trieuChung(request.getTrieuChung())
                .trangThai("CHO_XAC_NHAN")
                .nguonTao("ONLINE")
                .build();

        lh = lichHenRepository.save(lh);
        return mapToResponse(lh);
    }

    @Override
    public List<LichHenResponse> getAppointmentsByPatientId(Integer patientId) {
        List<LichHen> list = lichHenRepository.findByBenhNhanId(patientId);
        if (list.isEmpty()) {
            list = lichHenRepository.findByBenhNhanTaiKhoanId(patientId);
        }
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LichHenResponse cancelAppointment(Integer id) {
        LichHen lh = lichHenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại!"));
        lh.setTrangThai("DA_HUY");
        lh = lichHenRepository.save(lh);
        return mapToResponse(lh);
    }

    @Override
    @Transactional
    public LichHenResponse confirmPayment(Integer id) {
        LichHen lh = lichHenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại!"));
        lh.setTrangThai("DA_XAC_NHAN");
        lh = lichHenRepository.save(lh);
        return mapToResponse(lh);
    }
}
