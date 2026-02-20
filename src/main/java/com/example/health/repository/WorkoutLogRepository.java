package com.example.health.repository;

import com.example.health.domain.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    // ✨ 내 아이디(userId)와 일치하는 운동 기록만 싹 가져오는 마법의 코드
    List<WorkoutLog> findByUserId(String userId);
}