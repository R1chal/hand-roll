# HandMade 项目全面概览

## 📋 项目基本信息

**项目名称**: hand-roll (手写学习项目)
**项目位置**: `/Users/richal/Java/HandMade/hand-roll`
**项目类型**: Maven 多模块项目
**Java 版本**: 混合版本 (1.8 - 23)
**总文件数**: 41 个 Java 源文件
**项目描述**: 手写学习，仅供参考 - 这是一个学习型项目，通过手写实现 Java 核心数据结构和框架

---

## 🏗️ 项目目录结构

```
hand-roll/
├── pom.xml                          # 主 Maven 配置文件
├── README.md                        # 项目说明文档
├── .git/                            # Git 版本控制
├── .idea/                           # IntelliJ IDEA 配置
│
├── thread-pool/                     # 线程池模块
│   ├── pom.xml
│   └── src/main/java/com/richal/learn/
│       ├── MyThreadPool.java        # 自定义线程池实现
│       ├── RejectHandle.java        # 拒绝策略接口
│       ├── ThrowRejectHandle.java   # 抛异常拒绝策略
│       ├── DiscardRejectHandle.java # 丢弃拒绝策略
│       └── Main.java                # 线程池演示
│
├── hashmap/                         # HashMap 模块
│   ├── pom.xml
│   ├── src/main/java/com/richal/learn/
│   │   └── MyHashMap.java           # 自定义 HashMap 实现
│   └── src/test/java/
│       └── MyHashMapTest.java       # HashMap 测试
│
├── list/                            # 列表模块
│   ├── pom.xml
│   ├── src/main/java/com/richal/learn/
│   │   ├── List.java                # 列表接口
│   │   ├── MyArrayList.java         # 自定义 ArrayList 实现
│   │   └── MyLinkedList.java        # 自定义 LinkedList 实现
│   └── src/test/java/
│       ├── ArrayListTest.java       # ArrayList 测试
│       └── LinkedListTest.java      # LinkedList 测试
│
├── aqs-lock/                        # AQS 锁模块
│   ├── pom.xml
│   ├── src/main/java/com/richal/learn/
│   │   └── MyLock.java              # 基于 AQS 的自定义锁
│   └── src/test/java/
│       └── LockTest.java            # 锁测试
│
├── proxy_module/                    # 动态代理模块
│   ├── pom.xml
│   └── src/main/java/com/richal/learn/
│       ├── MyInterface.java         # 代理接口
│       ├── MyHandler.java           # 代理处理器接口
│       ├── MyInterfaceFactory.java  # 代理工厂
│       ├── MyInterfaceProxy0.java   # 生成的代理类示例
│       ├── Compiler.java            # 代码编译器
│       ├── NameAndLengthImpl.java    # 接口实现
│       └── Main.java                # 动态代理演示
│
└── spring-mini/                     # Spring 框架 Mini 版本
    ├── pom.xml
    ├── src/main/java/com/richal/learn/
    │   ├── ApplicationContext.java  # IOC 容器核心
    │   ├── BeanDefinition.java      # Bean 定义
    │   ├── BeanPostProcesser.java   # Bean 后处理器接口
    │   ├── MyBeanPostProcesser.java # 自定义后处理器
    │   ├── Component.java           # @Component 注解
    │   ├── Autowired.java           # @Autowired 注解
    │   ├── PostConstruct.java       # @PostConstruct 注解
    │   ├── Main.java                # Spring 演示
    │   ├── sub/
    │   │   ├── Cat.java             # 示例 Bean
    │   │   └── Dog.java             # 示例 Bean
    │   └── web/                     # Spring MVC 模块
    │       ├── DispatcherServlet.java    # 前端控制器
    │       ├── Controller.java           # @Controller 注解
    │       ├── RequestMapping.java       # @RequestMapping 注解
    │       ├── ResponseBody.java         # @ResponseBody 注解
    │       ├── ModelAndView.java         # 模型视图
    │       ├── WebHandler.java           # Web 处理器
    │       ├── TomCatServer.java         # Tomcat 服务器
    │       ├── User.java                 # 示例数据类
    │       ├── controller/
    │       │   ├── HelloController.java  # 示例控制器
    │       │   └── Param.java            # @Param 注解
    │       └── resources/                # 静态资源
    └── target/                      # 编译输出目录
```

---

## 📦 模块详细说明

### 1. thread-pool 模块 - 自定义线程池

**Java 版本**: 1.8
**依赖**: 无额外依赖

**核心功能**:
- `MyThreadPool`: 完整的线程池实现，支持核心线程、非核心线程、任务队列
- 参数: corePoolSize、maximumPoolSize、keepAliveTime、workQueue、rejectHandle、threadFactory
- 拒绝策略: ThrowRejectHandle (抛异常)、DiscardRejectHandle (丢弃)
- 内部 Worker 类处理任务执行和线程生命周期

**关键特性**:
- 使用 AtomicInteger 管理线程计数
- 支持自定义拒绝策略
- 核心线程无限期等待，非核心线程超时退出
- 完整的线程生命周期管理

---

### 2. hashmap 模块 - 自定义 HashMap

**Java 版本**: 17
**依赖**: JUnit 5

**核心功能**:
- `MyHashMap<K, V>`: 基于数组 + 链表的 HashMap 实现
- 初始容量: 16
- 扩容因子: 0.75
- 哈希冲突解决: 链表法

**关键方法**:
- `put(K key, V value)`: 添加或更新键值对
- `get(K key)`: 获取值
- `remove(K key)`: 删除键值对
- `size()`: 获取大小
- `indexOf(Object key)`: 计算哈希索引
- `resizeIfNecessary()`: 自动扩容

**实现细节**:
- 使用位运算 `key.hashCode() & (table.length - 1)` 计算索引
- 扩容时重新计算所有键的位置
- 采用头插法进行链表插入

---

### 3. list 模块 - 自定义列表

**Java 版本**: 23
**依赖**: JUnit 5

**核心实现**:

**MyArrayList<E>**:
- 基于动态数组实现
- 初始容量: 10
- 扩容策略: 翻倍扩容
- 时间复杂度: 添加 O(1) 平均、O(n) 最坏; 删除 O(n); 查询 O(1)

**MyLinkedList<E>**:
- 基于双向链表实现
- 支持从头尾两端查找优化
- 时间复杂度: 添加 O(1); 删除 O(n); 查询 O(n)
- 优化: 根据索引位置决定从头或尾开始查找

**共同接口**:
- `add(E element)`: 末尾添加
- `add(E element, int index)`: 指定位置插入
- `remove(int index)`: 按索引删除
- `remove(E element)`: 按值删除
- `get(int index)`: 获取元素
- `set(int index, E element)`: 修改元素
- `size()`: 获取大小
- `iterator()`: 获取迭代器

---

### 4. aqs-lock 模块 - 自定义锁 (AQS)

**Java 版本**: 1.8
**依赖**: JUnit 4

**核心功能**:
- `MyLock`: 基于 AQS 思想的自定义锁实现
- 使用 AtomicBoolean 管理锁状态
- 使用 AtomicReference 管理等待队列

**关键特性**:
- `lock()`: 获取锁，若失败则加入等待队列并阻塞
- `unlock()`: 释放锁并唤醒下一个等待线程
- 使用 LockSupport.park() 和 unpark() 进行线程阻塞/唤醒
- 等待队列采用双向链表结构
- 哨兵节点简化队列操作

**实现细节**:
- 快速路径: 直接尝试获取锁
- 慢速路径: 加入等待队列并自旋等待

---

### 5. proxy_module 模块 - 动态代理

**Java 版本**: 8
**依赖**: 无

**核心功能**:
- `MyInterfaceFactory`: 动态代理工厂
- 运行时生成代理类的 Java 源代码
- 编译生成的源代码为字节码
- 使用 URLClassLoader 动态加载代理类

**工作流程**:
1. 生成唯一的类名 (MyInterfaceProxy0, MyInterfaceProxy1...)
2. 根据处理器生成 Java 源代码
3. 将源代码写入文件系统
4. 使用 Compiler 编译源代码
5. 使用 URLClassLoader 加载编译后的类
6. 通过反射创建代理对象实例
7. 调用处理器的 setProxy 方法初始化

**关键类**:
- `MyInterface`: 代理接口，定义 method1、method2、method3
- `MyHandler`: 处理器接口，定义方法实现逻辑
- `Compiler`: 代码编译器，负责编译生成的源代码
- `MyInterfaceFactory`: 工厂类，协调整个代理创建过程

---

### 6. spring-mini 模块 - Spring 框架 Mini 版本

**Java 版本**: 23
**依赖**: Tomcat Embed、SLF4J、Logback、FastJSON2

**核心功能**:

**IOC 容器 (ApplicationContext)**:
- 包扫描: 扫描指定包下的所有 @Component 类
- Bean 定义注册: 将类信息封装为 BeanDefinition
- Bean 实例化: 通过反射创建 Bean 实例
- 依赖注入: 自动注入 @Autowired 标记的字段
- Bean 初始化: 执行 @PostConstruct 方法和后处理器
- 单例管理: 确保每个 Bean 只有一个实例
- 循环依赖处理: 通过早期引用解决

**Bean 生命周期**:
1. 扫描包获取类
2. 过滤 @Component 类
3. 封装为 BeanDefinition
4. 初始化 BeanPostProcessor
5. 创建所有 Bean 实例
6. 注入依赖
7. 执行初始化方法
8. 注册到 IOC 容器

**Spring MVC 模块**:
- `DispatcherServlet`: 前端控制器，统一处理所有请求
- 路由匹配: 使用 HashMap 进行 O(1) 路由查找
- 参数绑定: 支持 @Param 注解和参数名绑定
- 结果渲染: 支持 HTML、JSON、ModelAndView 三种返回类型
- 模板引擎: 极简模板引擎，支持 {{key}} 和 ${key} 占位符替换
- `TomCatServer`: 嵌入式 Tomcat 服务器

**注解系统**:
- `@Component`: 标记 Spring 组件
- `@Autowired`: 标记需要注入的字段
- `@PostConstruct`: 标记初始化方法
- `@Controller`: 标记控制器类
- `@RequestMapping`: 标记请求映射
- `@ResponseBody`: 标记 JSON 响应
- `@Param`: 标记请求参数

**关键类**:
- `BeanDefinition`: 封装 Bean 的元数据信息
- `BeanPostProcesser`: Bean 后处理器接口
- `MyBeanPostProcesser`: 自定义后处理器实现
- `WebHandler`: Web 请求处理器
- `ModelAndView`: 模型视图对象
- `HelloController`: 示例控制器

---

## 🔧 配置文件分析

### 主 pom.xml

```xml
<groupId>com.richal.learn</groupId>
<artifactId>hand-roll</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>pom</packaging>

<modules>
    <module>thread-pool</module>
    <module>hashmap</module>
    <module>list</module>
    <module>aqs-lock</module>
    <module>spring-mini</module>
    <module>proxy_module</module>
</modules>

<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### 各模块 Java 版本

- **thread-pool**: Java 1.8
- **hashmap**: Java 17
- **list**: Java 23
- **aqs-lock**: Java 1.8
- **proxy_module**: Java 8
- **spring-mini**: Java 23

### 依赖概览

- **spring-mini**:
  - org.apache.tomcat.embed:tomcat-embed-core:10.1.42
  - org.slf4j:jul-to-slf4j:2.0.17
  - ch.qos.logback:logback-classic:1.5.18
  - com.alibaba.fastjson2:fastjson2:2.0.53

- **list & hashmap**: JUnit 5 (5.8.2)
- **aqs-lock**: JUnit 4 (4.13.2)

---

## 📊 代码统计

- **总 Java 文件数**: 41 个
- **主要源文件**: 分布在 6 个模块中
- **测试文件**: 5 个测试类
- **代码组织**: 标准 Maven 项目结构 (src/main/java, src/test/java)

---

## 🎯 项目学习价值

这个项目通过手写实现以下核心 Java 知识点:

1. **数据结构**: HashMap、ArrayList、LinkedList 的内部实现原理
2. **并发编程**: 线程池、AQS 锁、原子变量、线程同步
3. **反射机制**: 动态代理、运行时代码生成和编译
4. **框架设计**: Spring IOC 容器、依赖注入、Bean 生命周期
5. **Web 框架**: Spring MVC、DispatcherServlet、请求路由
6. **设计模式**: 工厂模式、单例模式、责任链模式、策略模式

---

## 🔑 关键文件路径

| 模块 | 关键文件 |
|------|--------|
| thread-pool | `thread-pool/src/main/java/com/richal/learn/MyThreadPool.java` |
| hashmap | `hashmap/src/main/java/com/richal/learn/MyHashMap.java` |
| list | `list/src/main/java/com/richal/learn/MyArrayList.java` |
| list | `list/src/main/java/com/richal/learn/MyLinkedList.java` |
| aqs-lock | `aqs-lock/src/main/java/com/richal/learn/MyLock.java` |
| proxy_module | `proxy_module/src/main/java/com/richal/learn/MyInterfaceFactory.java` |
| spring-mini | `spring-mini/src/main/java/com/richal/learn/ApplicationContext.java` |
| spring-mini | `spring-mini/src/main/java/com/richal/learn/web/DispatcherServlet.java` |

---

## 📝 总结

这是一个非常优秀的学习项目，通过手写实现 Java 核心组件，深入理解了数据结构、并发编程、反射机制和框架设计的原理。每个模块都有清晰的代码注释和完整的实现，非常适合作为学习参考资料。
