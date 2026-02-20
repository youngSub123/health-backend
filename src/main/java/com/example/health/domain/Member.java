package com.example.health.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Member {

    @Id
    private String userId;   // 아이디 (PK)
    private String password; // 비밀번호
    private String name;     // 사용자 이름 (예: 신영섭)
    private String email; // 이메일

    public Member(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }
}