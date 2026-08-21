# netty-learning —— 用"受虐"的方式学 Netty

> 学习理念:**Netty 不是学出来的,是被原生 BIO/NIO 虐出来的。**
> 这个项目让你亲手把三个版本的服务器跑一遍,先看 BIO 怎么崩、NIO 怎么乱,
> 最后体会 Netty 一行代码解决问题的畅快。

## 场景

> 老板要你做一个聊天服务,**支撑 1 万人同时在线**。

我们分四幕走:

| 幕 | 版本 | 你会体会到 |
|---|---|---|
| 一 | `BioServer` | 一个连接一个线程,连接一多直接 OOM/卡死 |
| 二 | (线程池救火) | 池子被占满,后面的连接排队卡死 —— 换汤不换药 |
| 三 | `NioServer` | 单线程扛住了连接数,但粘包/拆包/各种天坑让你崩溃 |
| 四 | `NettyServer` | 上面所有问题,一行行解码器 + EventLoop 全解决了 |

## 快速开始

先编译:

```bash
cd netty-learning
mvn compile
```

---

### 🧪 实验 A:打爆 BIO(感受线程爆炸)

终端 1,启动 BIO 服务器:

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.bio.BioServer
```

终端 2,用 3000 个**空闲连接**打它(连上不发消息,纯占着):

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="3000 0"
```

**观察 BIO 控制台**:连接数一多,要么内存暴涨,要么抛
`OutOfMemoryError: unable to create native thread`。
这就是"一个连接一个线程"的死穴 —— 99% 的连接 99% 的时间在发呆,却每个都要养一个线程。

---

### 🧪 实验 B:打出粘包/拆包(感受 NIO 的混乱)

终端 1,启动手写 NIO 服务器(**故意没处理粘包**):

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.nio.NioServer
```

终端 2,用 1 个连接连发 10 条消息:

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="1 10"
```

**观察 NIO 控制台**:你会看到 10 条消息**粘成一坨**,或者被**切成碎片**。
TCP 是字节流,没有消息边界 —— 这就是压垮手写 NIO 的最后一根稻草。
要修它,你得手写协议解析状态机,代码量爆炸。

顺便数一数 `NioServer.java` 里有多少个 `⚠️` 标注的天坑。

---

### 🧪 实验 C:Netty 雨过天晴

终端 1,启动 Netty 服务器:

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.netty.NettyServer
```

终端 2,用**和实验 B 一模一样**的命令打它:

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="1 10"
```

**观察 Netty 控制台**:10 条消息**一条是一条,整整齐齐**。
差别只在 pipeline 里那一行 `new LineBasedFrameDecoder(1024)`。

再用实验 A 的 3000 连接打它:

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="3000 0"
```

Netty 版**毫无压力** —— 它只用 `CPU核数×2` 个线程,扛住了全部连接。

---

### 🧪 实验 D:WebSocket —— 亲手握一次手,才知道 Netty 多香

前面都是裸 TCP 字节流。这次升级成 **WebSocket**,先看手写原生要吃多少苦,再看 Netty 怎么一键抹平。

#### D-1 手写原生 WebSocket(回显)

终端启动:

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.ws.raw.RawWebSocketServer
```

双击打开项目里的 `web/chat.html`,地址填 `ws://127.0.0.1:8889` 点连接,发消息看回显。

**回头翻 `RawWebSocketServer.java` 里的 ⚠️**:HTTP Upgrade 握手、`SHA-1+Base64` 算 `Sec-WebSocket-Accept`、帧头按位解析(FIN/opcode/变长长度)、unmask、ping/pong…… 全是和业务无关的体力活。

#### D-2 Netty WebSocket 聊天室(广播)

```bash
mvn exec:java -Dexec.mainClass=com.study.netty.ws.chat.WsChatServer
```

`web/chat.html` 地址填 `ws://127.0.0.1:8888/ws`,**多开几个标签页**互发消息 —— 广播实时到达。

对照看:上面手写版的所有坑,在 Netty 里就是 pipeline 一行 `WebSocketServerProtocolHandler("/ws")`,握手、帧解析、心跳、ping/pong 全自动,你的业务只剩"收文本帧 → 广播"。

---

## 代码结构

```
src/main/java/com/study/netty/
├── common/Constants.java        # 端口等常量
├── bio/BioServer.java           # 第一幕:阻塞式,一个连接一个线程
├── nio/NioServer.java           # 第三幕:手写 NIO,各种天坑 ⚠️
├── netty/NettyServer.java       # 第四幕:Netty,雨过天晴 ✨
├── ws/raw/RawWebSocketServer.java   # 第五幕前传:手写原生 WebSocket ⚠️
├── ws/chat/WsChatServer.java        # 第五幕:Netty WebSocket 聊天室 ✨
└── stress/StressClient.java     # 压测客户端,用来"虐待"上面三个

web/chat.html                    # 浏览器测试页,双击即用
```

## 一句话记住这节课

> **BIO 死于线程模型,NIO 死于复杂度,Netty 把两者都解决了。**
