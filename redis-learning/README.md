# Redis 学习项目

连的是你云上的 Redis Cloud 实例，用 **Jedis** 客户端演示最常用的命令。每个类都能独立跑。

## 跑起来

```bash
cd redis-learning
mvn compile exec:java -Dexec.mainClass=com.study.redis.string.StringDemo
```

把 `StringDemo` 换成下面任意一个类名即可。

## 目录

| 类 | 演示 | 一句话 |
|---|---|---|
| `string.StringDemo` | String | set/get、过期、setnx 加锁、incr 计数 |
| `hash.HashDemo` | Hash | 存对象、只改一个字段、hincrby |
| `list.ListDemo` | List | 队列（lpush+rpop）、阻塞消费 blpop |
| `set.SetDemo` | Set | 点赞去重、共同好友（sinter） |
| `zset.ZSetDemo` | ZSet | 排行榜、前 N 名、查名次、加分 |
| `pubsub.PubSubDemo` | 发布订阅 | 实时广播，一发多收 |
| `pipeline.PipelineDemo` | Pipeline | 批量写提速对比（逐条 vs 打包） |

## 连接配置（密码不进 git）

连接信息在 `src/main/resources/redis.properties`，这个文件已被 `.gitignore` 忽略。
换机器 / 给别人用时：

```bash
cp src/main/resources/redis.example.properties src/main/resources/redis.properties
# 然后填自己的 host / port / password
```

## 一个值得跑一下的

`PipelineDemo` 是云上实例最能体现差距的——你的 Redis 在美东，单次网络往返就要上百毫秒，逐条写 5000 个 key 会非常慢，Pipeline 一次打包发过去能快几十倍。先跑它感受最深。
