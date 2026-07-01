package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.NhanVien;
import com.poly.hospital_support.entity.BioBacSi;
import com.poly.hospital_support.entity.BacSiKhoa;
import com.poly.hospital_support.repository.BioBacSiRepository;
import com.poly.hospital_support.repository.BacSiKhoaRepository;
import com.poly.hospital_support.service.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/bac-si")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NhanVienController {

    private final NhanVienService nhanVienService;
    private final BioBacSiRepository bioBacSiRepository;
    private final BacSiKhoaRepository bacSiKhoaRepository;

    @GetMapping
    public ResponseEntity<?> getAllDoctors(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String search
    ) {
        List<NhanVien> nhanViens = nhanVienService.getAllDoctors(departmentId, search);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (NhanVien nv : nhanViens) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", nv.getId());
            map.put("hoTen", nv.getHoTen());
            map.put("gioiTinh", nv.getGioiTinh());
            map.put("diaChi", nv.getDiaChi());
            map.put("ngaySinh", nv.getNgaySinh());
            map.put("ngayLam", nv.getNgayLam());
            map.put("taiKhoan", nv.getTaiKhoan());

            // Load department and position details from bac_si_khoa
            List<BacSiKhoa> bskList = bacSiKhoaRepository.findByNhanVienId(nv.getId());
            String primaryDept = "Khoa khám";
            String position = "Bác sĩ điều trị";
            List<Map<String, Object>> depts = new ArrayList<>();
            
            if (bskList != null && !bskList.isEmpty()) {
                primaryDept = bskList.get(0).getKhoa().getTenKhoa();
                if (bskList.get(0).getChucVu() != null) {
                    position = bskList.get(0).getChucVu();
                }
                for (BacSiKhoa bsk : bskList) {
                    Map<String, Object> dMap = new HashMap<>();
                    dMap.put("id", bsk.getKhoa().getId());
                    dMap.put("tenKhoa", bsk.getKhoa().getTenKhoa());
                    dMap.put("slug", bsk.getKhoa().getSlug());
                    dMap.put("chucVu", bsk.getChucVu() != null ? bsk.getChucVu() : "Bác sĩ điều trị");
                    depts.add(dMap);
                }
            }
            
            map.put("chuyenKhoa", primaryDept);
            map.put("chucVu", position);
            map.put("khoaList", depts);

            // Fetch bio if exists
            Optional<BioBacSi> bioOpt = Optional.empty();
            if (nv.getTaiKhoan() != null) {
                bioOpt = bioBacSiRepository.findByTaiKhoanId(nv.getTaiKhoan().getId());
            }
            map.put("bio", bioOpt.map(BioBacSi::getNoiDung).orElse(""));
            
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Integer id) {
        try {
            NhanVien nv = nhanVienService.getDoctorById(id);
            Optional<BioBacSi> bioOpt = Optional.empty();
            if (nv.getTaiKhoan() != null) {
                bioOpt = bioBacSiRepository.findByTaiKhoanId(nv.getTaiKhoan().getId());
            }
            String bioContent = bioOpt.map(BioBacSi::getNoiDung).orElse("");
            
            Map<String, Object> map = new HashMap<>();
            map.put("id", nv.getId());
            map.put("hoTen", nv.getHoTen());
            map.put("gioiTinh", nv.getGioiTinh());
            map.put("diaChi", nv.getDiaChi());
            map.put("ngaySinh", nv.getNgaySinh());
            map.put("ngayLam", nv.getNgayLam());
            map.put("taiKhoan", nv.getTaiKhoan());
            
            List<BacSiKhoa> bskList = bacSiKhoaRepository.findByNhanVienId(nv.getId());
            String primaryDept = "Khoa khám";
            String position = "Bác sĩ điều trị";
            List<Map<String, Object>> depts = new ArrayList<>();
            
            if (bskList != null && !bskList.isEmpty()) {
                primaryDept = bskList.get(0).getKhoa().getTenKhoa();
                if (bskList.get(0).getChucVu() != null) {
                    position = bskList.get(0).getChucVu();
                }
                for (BacSiKhoa bsk : bskList) {
                    Map<String, Object> dMap = new HashMap<>();
                    dMap.put("id", bsk.getKhoa().getId());
                    dMap.put("tenKhoa", bsk.getKhoa().getTenKhoa());
                    dMap.put("slug", bsk.getKhoa().getSlug());
                    dMap.put("chucVu", bsk.getChucVu() != null ? bsk.getChucVu() : "Bác sĩ điều trị");
                    depts.add(dMap);
                }
            }
            
            map.put("chuyenKhoa", primaryDept);
            map.put("chucVu", position);
            map.put("khoaList", depts);
            map.put("bio", bioContent);

            return ResponseEntity.ok(Map.of(
                    "nhanVien", map,
                    "bio", bioContent
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
