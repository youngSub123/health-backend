package com.example.health.repository;

import com.example.health.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    // 아이디와 비밀번호가 동시에 일치하는 회원 찾기 (로그인용)
    Optional<Member> findByUserIdAndPassword(String userId, String password);
}