package com.example.health.controller;

import com.example.health.domain.UserProfile;
import com.example.health.repository.UserProfileRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserProfileRepository repository;

    public ProfileController(UserProfileRepository repository) {
        this.repository = repository;
    }

    // 프로필 가져오기
    @GetMapping("/{userId}")
    public UserProfile getProfile(@PathVariable String userId) {
        // 없으면 기본값(키 175, 몸무게 70, 단백질 112)으로 임시 객체 반환
        return repository.findById(userId)
                .orElse(new UserProfile(userId, 175.0, 70.0, 112));
    }

    // 프로필 저장/수정하기
    @PostMapping
    public String saveProfile(@RequestBody UserProfile profile) {
        repository.save(profile);
        return "프로필이 성공적으로 저장되었습니다! 💪";
    }
}