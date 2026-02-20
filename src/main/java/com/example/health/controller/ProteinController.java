package com.example.health.controller;

import com.example.health.domain.ProteinRecord;
import com.example.health.repository.ProteinRecordRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/protein")
public class ProteinController {

    private final ProteinRecordRepository repository;

    public ProteinController(ProteinRecordRepository repository) {
        this.repository = repository;
    }

    // 1. 오늘 먹은 단백질량 조회
    @GetMapping("/{userId}")
    public int getTodayProtein(@PathVariable String userId) {
        return repository.findByUserIdAndRecordDate(userId, LocalDate.now())
                .map(ProteinRecord::getTotalProtein)
                .orElse(0);
    }

    // 2. 단백질 섭취량 추가 (기존 섭취량에 누적)
    @PostMapping("/{userId}")
    public int addProtein(@PathVariable String userId, @RequestParam int amount) {
        LocalDate today = LocalDate.now();
        ProteinRecord record = repository.findByUserIdAndRecordDate(userId, today)
                .orElse(new ProteinRecord(userId, today, 0));

        record.setTotalProtein(record.getTotalProtein() + amount);
        repository.save(record);

        return record.getTotalProtein();
    }

    // 3. 오늘 섭취량 초기화
    @DeleteMapping("/{userId}")
    public void resetProtein(@PathVariable String userId) {
        repository.findByUserIdAndRecordDate(userId, LocalDate.now())
                .ifPresent(repository::delete);
    }
}