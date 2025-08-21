FROM gradle:8.8-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle ./
COPY gradle ./gradle
COPY order-platform-msa-review ./order-platform-msa-review
COPY order-platform-msa-review/build.cloud.gradle ./order-platform-msa-review/build.gradle

RUN ./gradlew :order-platform-msa-review:build -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/order-platform-msa-review/build/libs/*.jar /app/application.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
