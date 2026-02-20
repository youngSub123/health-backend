package com.example.health.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
public class ProteinRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private LocalDate recordDate;
    private int totalProtein; // 오늘 먹은 총 단백질(g)

    public ProteinRecord(String userId, LocalDate recordDate, int totalProtein) {
        this.userId = userId;
        this.recordDate = recordDate;
        this.totalProtein = totalProtein;
    }
}