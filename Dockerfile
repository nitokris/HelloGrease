# ---- 构建阶段 ----
FROM gradle:jdk17 AS builder

WORKDIR /app

# 设置 JVM 内存与禁用守护进程，防止在 Docker 内 GC 暴毙卡死
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=\"-Xmx2048m -XX:+HeapDumpOnOutOfMemoryError\""

# 1. 先复制依赖配置文件
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# 2. 预下载项目依赖（代替原先会报错的 gradle build）
RUN gradle dependencies --no-daemon

# 3. 复制源码
COPY src ./src

# 4. 执行打包（去掉了会清空缓存的 clean，保留 --build-cache）
RUN gradle bootJar --no-daemon --build-cache -x test

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["sh", "-c", "java -jar /app/app.jar"]