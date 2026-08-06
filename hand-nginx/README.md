# 手写 Nginx (Java版) —— 理解 Nginx 的设计哲学

> 用 Java 手写简化版 Nginx，核心目标是理解其设计思想。

## 目录结构

```
hand-nginx/
├── src/main/java/com/handnginx/
│   ├── HandNginx.java              # 第1章：程序入口
│   ├── config/
│   │   └── NginxConfig.java        # 第2章：配置文件解析
│   ├── connection/
│   │   └── Connection.java         # 第3章：连接管理
│   ├── http/
│   │   └── HttpRequest.java        # 第3章：HTTP 请求处理
│   ├── location/
│   │   └── LocationMatcher.java    # 第4章：location 匹配
│   ├── proxy/
│   │   └── ReverseProxy.java       # 第5章：反向代理
│   └── event/
│       └── EventLoop.java          # 第6章：事件驱动循环
├── conf/
│   └── nginx.conf                  # 配置文件
└── pom.xml
```

## Nginx 核心设计思想

| 设计思想 | Nginx 实现 | 手写版体现 |
|---------|-----------|-----------|
| **事件驱动** | epoll + 事件循环 | Selector + 事件循环 |
| **异步非阻塞** | 单线程处理万连接 | 单 Selector 线程 + 线程池处理 |
| **模块化** | 模块动态加载 | 接口 + 实现分离 |
| **内存池** | 减少 malloc/free | 对象复用 |
| **配置驱动** | nginx.conf 驱动行为 | Properties 配置驱动 |

## 6 章内容

### 第1章：main() 程序入口
- 配置驱动启动
- 模块化设计
- 事件循环驱动

### 第2章：配置文件解析
- 层级结构（http/server/location）
- 指令驱动行为
- 配置即代码

### 第3章：HTTP 请求处理
- 连接管理（Connection）
- 请求解析（HttpRequest）
- 状态机设计

### 第4章：location 匹配
- 最长前缀匹配
- 优先级顺序
- 匹配后停止

### 第5章：反向代理
- 上游连接转发
- 负载均衡（简化版）
- 超时处理

### 第6章：事件驱动循环
- Selector 替代 epoll
- 非阻塞 I/O
- 单线程事件循环

## 运行方式

```bash
# 编译
mvn compile

# 运行
mvn exec:java -Dexec.mainClass="com.handnginx.HandNginx"

# 或直接运行
java -cp target/classes com.handnginx.HandNginx
```

## 测试

```bash
# 启动后访问
curl http://localhost:8080/
```
