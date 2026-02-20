package com.example.health.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateExtractor {

    // 1. 기호 패턴 (예: 2026.02.05 또는 2026-02-05)
    private static final Pattern PATTERN_SYMBOL = Pattern.compile("20\\d{2}[.\\-/]\\d{1,2}[.\\-/]\\d{1,2}");

    // 2. ✨ 한글 패턴 추가 (예: 2026년 2월 5일) - 띄어쓰기 있어도 됨
    private static final Pattern PATTERN_KOREAN = Pattern.compile("20\\d{2}년\\s*\\d{1,2}월\\s*\\d{1,2}일");

    public static LocalDate extractDate(String text) {
        if (text == null) return null;

        // 1단계: 한글 패턴 먼저 찾아보기 ("2026년 2월 5일")
        Matcher koMatcher = PATTERN_KOREAN.matcher(text);
        if (koMatcher.find()) {
            String dateStr = koMatcher.group();
            System.out.println("찾은 날짜(한글): " + dateStr);

            // "2026년 2월 5일" -> "2026-2-5" 형식으로 변환
            dateStr = dateStr.replaceAll("년", "-")
                    .replaceAll("월", "-")
                    .replaceAll("일", "")
                    .replaceAll(" ", ""); // 공백 제거

            return parseDate(dateStr);
        }

        // 2단계: 기호 패턴 찾아보기 ("2026.02.05")
        Matcher symMatcher = PATTERN_SYMBOL.matcher(text);
        if (symMatcher.find()) {
            String dateStr = symMatcher.group();
            System.out.println("찾은 날짜(기호): " + dateStr);

            // ".", "/" 등을 "-"로 통일
            dateStr = dateStr.replaceAll("[./]", "-");
            return parseDate(dateStr);
        }

        return null; // 못 찾음
    }

    private static LocalDate parseDate(String dateStr) {
        try {
            // yyyy-M-d 패턴은 "2026-02-05"와 "2026-2-5" 둘 다 해석 가능!
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-M-d"));
        } catch (Exception e) {
            System.out.println("날짜 변환 중 오류 발생: " + dateStr);
            return null;
        }
    }
}