FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test
RUN rm -f build/libs/*-plain.jar
CMD ["sh", "-c", "java -jar build/libs/*.jar --server.port=${PORT:-8080} --spring.config.additional-location=optional:file:/etc/secrets/application.properties"]