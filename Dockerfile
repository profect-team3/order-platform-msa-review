# Stage 1: Build the application
FROM gradle:8.8-jdk17 AS builder
WORKDIR /workspace

# 프로젝트 설정 파일 복사
COPY settings.gradle* build.gradle* gradle.properties* /workspace/
COPY src /workspace/src

# Gradle 실행권한 부여
RUN chmod +x ./gradlew

# 빌드 (테스트 제외)
RUN ./gradlew build -x test

# Stage 2: Create the final, lightweight image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Gradle 빌드 결과물만 복사
COPY --from=builder /workspace/build/libs/*.jar /app/application.jar

# 포트 설정
EXPOSE 8086

# 실행
ENTRYPOINT ["java", "-jar", "/app/application.jar"]