package com.poly.hospital_support.service;

import com.poly.hospital_support.dto.TinTucRequest;
import com.poly.hospital_support.dto.TinTucResponse;

import java.util.List;

public interface TinTucService {

    // Lấy tất cả tin tức
    List<TinTucResponse> getAllTinTuc();

    // Lấy tin theo ID
    TinTucResponse getTinTucById(Integer id);

    // Thêm tin tức
    TinTucResponse createTinTuc(TinTucRequest request);

    // Cập nhật tin tức
    TinTucResponse updateTinTuc(Integer id, TinTucRequest request);

    // Xóa tin tức
    void deleteTinTuc(Integer id);

    // Danh sách tin đã ghim
    List<TinTucResponse> getTinDaGhim();

    // Ghim hoặc bỏ ghim
    TinTucResponse ghimTinTuc(Integer id);

}