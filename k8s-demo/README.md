# HandRoll K8s 部署示例

## 项目结构

```
hand-roll/
├── hand-nginx/          # Nginx 手写实现（已有模块）
├── spring-mini/         # Spring 手写实现（已有模块）
└── k8s-demo/            # K8s 部署示例模块（新增）
    ├── src/main/java/com/richal/learn/k8s/
    │   └── K8sDemoApplication.java
    ├── src/main/resources/
    │   └── application.yml
    ├── Dockerfile
    └── pom.xml
```

## 快速开始

### 1. 构建镜像

```bash
# 进入项目根目录
cd /Users/richal/Java/HandMade/hand-roll

# 编译打包
mvn clean package -pl k8s-demo -am

# 构建 Docker 镜像
docker build -t hand-roll-k8s-demo:latest k8s-demo/
```

### 2. 部署到 K8s

```bash
# 创建 Namespace
kubectl create namespace hand-roll

# 部署应用
kubectl apply -f k8s-demo/k8s/

# 查看状态
kubectl get pods -n hand-roll
```

### 3. 访问应用

```bash
# 端口转发
kubectl port-forward svc/k8s-demo-service 8080:8080 -n hand-roll

# 浏览器访问
http://localhost:8080/hello
```

## K8s 资源说明

| 资源 | 文件 | 作用 |
|------|------|------|
| Namespace | namespace.yaml | 资源隔离 |
| Deployment | deployment.yaml | 应用部署 |
| Service | service.yaml | 服务暴露 |
| ConfigMap | configmap.yaml | 配置管理 |

## 学习要点

1. **Dockerfile** - 如何将 Java 应用打包成镜像
2. **Deployment** - 如何管理 Pod
3. **Service** - 如何暴露服务
4. **ConfigMap** - 如何管理配置
5. **Namespace** - 如何隔离资源
