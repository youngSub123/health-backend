package com.example.health.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleOcrService {

    // 아까 넣은 키 파일 이름
    private static final String KEY_FILE_NAME = "google-key.json";

    public String extractText(MultipartFile file) throws IOException {

        // 1. 키 파일 읽어서 인증 정보 만들기
        InputStream keyStream = getClass().getClassLoader().getResourceAsStream(KEY_FILE_NAME);
        if (keyStream == null) {
            throw new IOException("구글 키 파일(" + KEY_FILE_NAME + ")을 찾을 수 없습니다. resources 폴더를 확인하세요.");
        }
        GoogleCredentials credentials = GoogleCredentials.fromStream(keyStream);

        // 2. 구글 비전 클라이언트 설정
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {

            // 3. 이미지 데이터를 구글이 원하는 포맷으로 변환
            ByteString imgBytes = ByteString.readFrom(file.getInputStream());
            Image img = Image.newBuilder().setContent(imgBytes).build();

            // 4. "텍스트를 읽어달라(TEXT_DETECTION)"고 요청서 작성
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            // 5. 진짜 요청 보내기! (구글 서버로 날아감 ✈️)
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            // 6. 결과 받아서 텍스트만 뽑아내기
            StringBuilder sb = new StringBuilder();
            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.println("구글 API 에러: " + res.getError().getMessage());
                    return null;
                }
                // 전체 텍스트 가져오기
                sb.append(res.getFullTextAnnotation().getText());
            }

            return sb.toString();
        }
    }
}