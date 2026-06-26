package com.poly.hospital_support.controller;

import com.poly.hospital_support.entity.TinTuc;
import com.poly.hospital_support.service.TinTucService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tin-tuc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TinTucController {

    private final TinTucService tinTucService;

    @GetMapping
    public ResponseEntity<Page<TinTuc>> getNews(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<TinTuc> newsPage = tinTucService.getNews(categoryId, search, pageable);
        return ResponseEntity.ok(newsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable Integer id) {
        try {
            TinTuc news = tinTucService.getNewsById(id);
            return ResponseEntity.ok(news);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getNewsBySlug(@PathVariable String slug) {
        try {
            TinTuc news = tinTucService.getNewsBySlug(slug);
            return ResponseEntity.ok(news);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
