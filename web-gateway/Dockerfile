FROM gradle:8.10-jdk21 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY src src/

RUN gradle build -x test

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/web-gateway.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]