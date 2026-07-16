# ---- 构建阶段 ----
FROM gradle:jdk17 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle build -x test --no-daemon --build-cache || true

COPY src ./src
RUN gradle clean bootJar --no-daemon --build-cache

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["sh", "-c", "java -jar /app/app.jar"]
