package com.example.health.dto;

// record: 게터, 세터, 생성자를 자동으로 만들어주는 데이터 전용 박스 (Java 17 신기능!)
public record WorkoutRequest(
        String name,
        int setNum,
        int weight,
        int reps
) {}