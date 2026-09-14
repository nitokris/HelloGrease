# ---- 构建阶段 ----
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 设置 Maven JVM 内存，防止在 Docker 内 OOM
ENV MAVEN_OPTS="-Xmx2048m -XX:+HeapDumpOnOutOfMemoryError"

# 1. 先复制 pom 文件
COPY pom.xml ./

# 2. 预下载项目依赖（利用 Docker 层缓存）
RUN mvn dependency:go-offline -B

# 3. 复制源码
COPY src ./src

# 4. 执行打包（跳过测试）
RUN mvn package -B -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

CMD ["sh", "-c", "java -jar /app/app.jar"]
