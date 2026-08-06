# 使用 Eclipse Temurin JDK 8 基础镜像
FROM eclipse-temurin:8-jdk

# 设置工作目录
WORKDIR /app

# 复制编译好的 jar 包
COPY target/k8s-demo-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
