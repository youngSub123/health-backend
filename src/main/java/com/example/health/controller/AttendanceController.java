package com.example.health.controller;

import com.example.health.domain.Attendance;
import com.example.health.repository.AttendanceRepository;
import com.example.health.service.GoogleOcrService; // ✨ 서비스 추가
import com.example.health.util.DateExtractor; // 날짜 추출기 (재사용)
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository repository;
    private final GoogleOcrService ocrService; // ✨ 추가
    private final String uploadDir = "uploads/";

    // 생성자 주입
    public AttendanceController(AttendanceRepository repository, GoogleOcrService ocrService) {
        this.repository = repository;
        this.ocrService = ocrService; // ✨ 추가

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
    }

    @PostMapping
    public String checkAttendance(@RequestParam("userId") String userId,
                                  @RequestParam("image") MultipartFile file) {
        try {
            // (1) 파일 저장
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            // (2) ✨ 구글 비전으로 텍스트 추출!
            System.out.println("구글에게 이미지를 분석 요청합니다...");
            String resultText = ocrService.extractText(file);
            System.out.println("구글이 읽은 내용:\n" + resultText);

            // (3) 텍스트에서 날짜 찾기 (아까 만든 DateExtractor 사용)
            LocalDate detectedDate = DateExtractor.extractDate(resultText);
            System.out.println("날짜 변환 내용:\n" + detectedDate);

            // (4) 결과 처리
            LocalDate finalDate;
            String message;

            if (detectedDate != null) {
                finalDate = detectedDate;
                message = "🔥 구글 AI가 날짜(" + finalDate + ")를 찾았습니다! 인증 성공!";
            } else {
                finalDate = LocalDate.now();
                message = "날짜를 못 찾아서 오늘(" + finalDate + ")로 출석합니다.";
            }

            // (5) DB 저장 (중복 체크)
            if (repository.findByUserIdAndWorkoutDate(userId, finalDate).isPresent()) {
                return "이미 " + finalDate + "에 출석하셨습니다! 💪";
            }

            repository.save(new Attendance(userId, finalDate, fileName));
            return message;

        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생: " + e.getMessage();
        }
    }

    // 2. 내 출석 기록 가져오기 (달력 표시용)
    @GetMapping("/{userId}")
    public List<Attendance> getMyAttendance(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }

    // 3. 데이터 전체 초기화 (DB + 파일 삭제)
    @DeleteMapping("/reset")
    public String resetData() {
        // 1. DB 내용 싹 지우기
        repository.deleteAll();

        // 2. 실제 사진 파일들 싹 지우기
        File dir = new File(uploadDir);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete(); // 파일 하나씩 삭제
            }
        }

        return "모든 데이터와 사진이 초기화되었습니다! ✨";
    }
}