package com.example.health.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // 회원 ID (일단은 이름이나 ID를 직접 입력받는 걸로!)

    private LocalDate workoutDate; // 운동한 날짜 (예: 2026-02-10)

    private String imageUrl; // 업로드한 인증샷 파일 경로

    // 생성자
    public Attendance(String userId, LocalDate workoutDate, String imageUrl) {
        this.userId = userId;
        this.workoutDate = workoutDate;
        this.imageUrl = imageUrl;
    }
}