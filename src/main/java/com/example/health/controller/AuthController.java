package com.example.health.controller;

import com.example.health.domain.Member;
import com.example.health.repository.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberRepository repository;

    public AuthController(MemberRepository repository) {
        this.repository = repository;
    }

    // 1. 회원가입 API
    @PostMapping("/signup")
    public String signup(@RequestBody Member member) {
        if (repository.existsById(member.getUserId())) {
            return "fail: 이미 존재하는 아이디입니다.";
        }
        repository.save(member);
        return "success: 회원가입이 완료되었습니다!";
    }

    // 2. 로그인 API
    @PostMapping("/login")
    public Member login(@RequestBody Map<String, String> loginData) {
        String userId = loginData.get("userId");
        String password = loginData.get("password");

        // 아이디와 비밀번호가 일치하면 회원 정보(Member) 반환, 틀리면 null 반환
        return repository.findByUserIdAndPassword(userId, password).orElse(null);
    }
}