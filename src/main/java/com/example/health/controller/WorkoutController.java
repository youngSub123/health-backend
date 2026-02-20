package com.example.health.controller;

import com.example.health.domain.WorkoutLog;
import com.example.health.repository.WorkoutLogRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutLogRepository repository;

    public WorkoutController(WorkoutLogRepository repository) {
        this.repository = repository;
    }

    // ✨ 1. 내 운동 기록만 가져오기 (주소에 아이디를 포함)
    @GetMapping("/list/{userId}")
    public List<WorkoutLog> getMyWorkouts(@PathVariable String userId) {
        return repository.findByUserId(userId); // 내 것만 찾아서 반환!
    }

    // ✨ 2. 기록 추가할 때 프론트에서 넘어온 userId도 같이 DB에 저장됨
    @PostMapping
    public String addWorkout(@RequestBody WorkoutLog workout) {
        repository.save(workout);
        return "success";
    }

    // 3. 수정 (기존과 거의 동일)
    @PutMapping("/{id}")
    public String updateWorkout(@PathVariable Long id, @RequestBody WorkoutLog newWorkout) {
        return repository.findById(id).map(workout -> {
            workout.setExerciseName(newWorkout.getExerciseName());
            workout.setWeight(newWorkout.getWeight());
            workout.setSetNum(newWorkout.getSetNum());
            workout.setReps(newWorkout.getReps());
            repository.save(workout);
            return "updated";
        }).orElse("fail");
    }

    // 4. 삭제 (기존과 동일)
    @DeleteMapping("/{id}")
    public String deleteWorkout(@PathVariable Long id) {
        repository.deleteById(id);
        return "deleted";
    }
}