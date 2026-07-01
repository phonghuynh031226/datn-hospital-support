package com.poly.hospital_support.impl;

import com.poly.hospital_support.entity.ViTriPhong;
import com.poly.hospital_support.repository.ViTriPhongRepository;
import com.poly.hospital_support.service.ViTriPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViTriPhongServiceImpl implements ViTriPhongService {

    private final ViTriPhongRepository viTriPhongRepository;

    @Override
    public List<ViTriPhong> getRooms() {
        return viTriPhongRepository.findAll();
    }
}
