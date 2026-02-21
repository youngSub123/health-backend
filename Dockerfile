# 1. Java 17 환경 세팅 (가장 빠르고 가벼운 알파인 버전)
FROM eclipse-temurin:17-jdk-alpine

# 2. 클라우드 서버 내부에 작업 폴더 생성
WORKDIR /app

# 3. 영섭님의 모든 코드를 서버 안으로 복사
COPY . .

# 4. 윈도우에서 만든 빌드 파일에 실행 권한 부여
RUN chmod +x ./gradlew

# 5. 테스트를 건너뛰고 프로젝트 진짜 빌드(포장)
RUN ./gradlew clean build -x test

# 6. 실행 시 충돌 방지를 위해 불필요한 파일(-plain.jar) 삭제
RUN rm -f build/libs/*-plain.jar

# 7. Render가 몰래 숨겨둔 비밀번호 파일(/etc/secrets/)을 읽으면서 서버 켜기!
CMD ["sh", "-c", "java -jar build/libs/*.jar --server.port=${PORT:-8080} --spring.config.additional-location=optional:file:/etc/secrets/application.properties"]