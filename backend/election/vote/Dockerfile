# docker build -t vote:latest -f Dockerfile .

FROM gradle:8.7.0-jdk17 AS builder
WORKDIR /app
COPY .. .
RUN ./gradlew clean build -x test

# 2단계: 실행
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/vote-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]