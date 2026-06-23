package com.poly.hospital_support.service;



import com.poly.hospital_support.entity.BenhAn;
import com.poly.hospital_support.repository.BenhAnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BenhAnService {
    @Autowired
    private BenhAnRepository benhAnRepo;

    public List<BenhAn> findAll() {
        return benhAnRepo.findAll();
    }

    public Optional<BenhAn> findById(Integer id) {
        return benhAnRepo.findById(id);
    }

    public BenhAn save(BenhAn benhAn) {
        return benhAnRepo.save(benhAn);
    }

    public void deleteById(Integer id) {
        benhAnRepo.deleteById(id);
    }
}