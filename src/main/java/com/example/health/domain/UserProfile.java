package com.example.health.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class UserProfile {
    @Id
    private String userId; // PK로 사용 (1명당 1개의 프로필만 가짐)

    private double height;
    private double weight;
    private int targetProtein;

    public UserProfile(String userId, double height, double weight, int targetProtein) {
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.targetProtein = targetProtein;
    }
}