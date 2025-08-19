# Stage 1: Build
FROM gradle:8.8-jdk17 AS builder
WORKDIR /workspace

# 1. 전체 프로젝트의 Gradle 설정 파일들을 먼저 복사합니다.
# 이 파일들이 변경될 때만 Gradle 의존성을 새로 다운로드하게 됩니다.
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

# 2. 빌드에 필요한 'review' 모듈의 소스 코드만 복사합니다.
# '.' 대신 특정 모듈 폴더를 지정하여 다른 모듈의 변경에 영향을 받지 않도록 합니다.
COPY order-platform-msa-review ./order-platform-msa-review

# 3. 전체 프로젝트 컨텍스트에서 특정 모듈을 빌드합니다.
RUN ./gradlew :order-platform-msa-review:build -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/order-platform-msa-review/build/libs/*.jar /app/application.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
