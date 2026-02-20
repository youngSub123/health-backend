package com.example.health.repository;

import com.example.health.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // 특정 유저의 특정 날짜 기록 찾기 (중복 출석 방지용)
    Optional<Attendance> findByUserIdAndWorkoutDate(String userId, LocalDate workoutDate);

    // 특정 유저의 모든 기록 가져오기 (달력에 뿌려주기 용)
    List<Attendance> findByUserId(String userId);
}