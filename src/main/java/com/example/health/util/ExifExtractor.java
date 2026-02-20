package com.example.health.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ExifExtractor {

    public static LocalDate getDateTaken(File file) {
        try {
            // 사진 파일에서 메타데이터 읽기
            Metadata metadata = ImageMetadataReader.readMetadata(file);

            // EXIF 정보 중 '촬영 정보' 디렉토리 가져오기
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (directory != null) {
                // "Date/Time Original" 태그 읽기
                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

                if (date != null) {
                    // Date -> LocalDate 변환
                    return date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }
            }
        } catch (Exception e) {
            System.out.println("EXIF 읽기 실패: " + e.getMessage());
        }

        // 정보가 없으면 null 반환
        return null;
    }
}