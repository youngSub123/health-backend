package com.example.health.repository;

import com.example.health.domain.ProteinRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ProteinRecordRepository extends JpaRepository<ProteinRecord, Long> {
    Optional<ProteinRecord> findByUserIdAndRecordDate(String userId, LocalDate recordDate);
}