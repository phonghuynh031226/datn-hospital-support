package com.poly.hospital_support.impl;

import com.poly.hospital_support.dto.TinTucRequest;
import com.poly.hospital_support.dto.TinTucResponse;
import com.poly.hospital_support.entity.TinTuc;
import com.poly.hospital_support.repository.TinTucRepository;
import com.poly.hospital_support.service.TinTucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TinTucServiceImpl implements TinTucService {

    @Autowired
    private TinTucRepository tinTucRepository;

    @Override
    public List<TinTucResponse> getAllTinTuc() {
        return tinTucRepository.findAllByOrderByNgayTaoDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public TinTucResponse getTinTucById(Integer id) {
        TinTuc tinTuc = tinTucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức có ID: " + id));

        return convertToResponse(tinTuc);
    }

    @Override
    @Transactional
    public TinTucResponse createTinTuc(TinTucRequest request) {
        TinTuc tinTuc = new TinTuc();

        tinTuc.setTieuDe(request.getTieuDe());
        tinTuc.setSlug(taoSlug(request.getTieuDe()));
        tinTuc.setTomTat(request.getTomTat());
        tinTuc.setNoiDung(request.getNoiDung());
        tinTuc.setHinhAnh(request.getHinhAnh());
        tinTuc.setTacGia(request.getTacGia());
        tinTuc.setDanhMuc(request.getDanhMuc());

        Boolean muonGhim = request.getDaGhim() != null && request.getDaGhim();

        if (muonGhim) {
            long soTinDaGhim = tinTucRepository.countByDaGhimTrue();

            if (soTinDaGhim >= 5) {
                throw new RuntimeException("Chỉ được ghim tối đa 5 bài tin tức!");
            }

            tinTuc.setDaGhim(true);
        } else {
            tinTuc.setDaGhim(false);
        }

        tinTuc.setNgayTao(LocalDateTime.now());
        tinTuc.setNgayCapNhat(LocalDateTime.now());

        TinTuc saved = tinTucRepository.save(tinTuc);

        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public TinTucResponse updateTinTuc(Integer id, TinTucRequest request) {
        TinTuc tinTuc = tinTucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức có ID: " + id));

        tinTuc.setTieuDe(request.getTieuDe());
        tinTuc.setSlug(taoSlug(request.getTieuDe()));
        tinTuc.setTomTat(request.getTomTat());
        tinTuc.setNoiDung(request.getNoiDung());
        tinTuc.setHinhAnh(request.getHinhAnh());
        tinTuc.setTacGia(request.getTacGia());
        tinTuc.setDanhMuc(request.getDanhMuc());

        if (request.getDaGhim() != null) {
            boolean dangBoGhim = tinTuc.getDaGhim() != null && tinTuc.getDaGhim();
            boolean muonGhim = request.getDaGhim();

            if (!dangBoGhim && muonGhim) {
                long soTinDaGhim = tinTucRepository.countByDaGhimTrue();

                if (soTinDaGhim >= 5) {
                    throw new RuntimeException("Chỉ được ghim tối đa 5 bài tin tức!");
                }
            }

            tinTuc.setDaGhim(muonGhim);
        }

        tinTuc.setNgayCapNhat(LocalDateTime.now());

        TinTuc saved = tinTucRepository.save(tinTuc);

        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTinTuc(Integer id) {
        TinTuc tinTuc = tinTucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức có ID: " + id));

        tinTucRepository.delete(tinTuc);
    }

    @Override
    public List<TinTucResponse> getTinDaGhim() {
        return tinTucRepository.findByDaGhimTrue()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public TinTucResponse ghimTinTuc(Integer id) {
        TinTuc tinTuc = tinTucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức có ID: " + id));

        boolean dangGhim = tinTuc.getDaGhim() != null && tinTuc.getDaGhim();

        if (dangGhim) {
            tinTuc.setDaGhim(false);
        } else {
            long soTinDaGhim = tinTucRepository.countByDaGhimTrue();

            if (soTinDaGhim >= 5) {
                throw new RuntimeException("Chỉ được ghim tối đa 5 bài tin tức!");
            }

            tinTuc.setDaGhim(true);
        }

        tinTuc.setNgayCapNhat(LocalDateTime.now());

        TinTuc saved = tinTucRepository.save(tinTuc);

        return convertToResponse(saved);
    }

    private TinTucResponse convertToResponse(TinTuc tinTuc) {
        TinTucResponse response = new TinTucResponse();

        response.setId(tinTuc.getId());
        response.setTieuDe(tinTuc.getTieuDe());
        response.setTomTat(tinTuc.getTomTat());
        response.setNoiDung(tinTuc.getNoiDung());
        response.setHinhAnh(tinTuc.getHinhAnh());
        response.setTacGia(tinTuc.getTacGia());
        response.setDanhMuc(tinTuc.getDanhMuc());
        response.setDaGhim(tinTuc.getDaGhim());
        response.setNgayTao(tinTuc.getNgayTao());
        response.setNgayCapNhat(tinTuc.getNgayCapNhat());

        return response;
    }

    private String taoSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "tin-tuc-" + System.currentTimeMillis();
        }

        String slug = text.toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[íìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return slug + "-" + System.currentTimeMillis();
    }
}