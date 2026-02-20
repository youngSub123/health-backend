package com.example.health.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // ✨ 누가 운동했는지 기록할 아이디 추가!
    private String exerciseName;
    private int weight;
    private int setNum;
    private int reps;

    public WorkoutLog(String userId, String exerciseName, int weight, int setNum, int reps) {
        this.userId = userId;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.setNum = setNum;
        this.reps = reps;
    }
}