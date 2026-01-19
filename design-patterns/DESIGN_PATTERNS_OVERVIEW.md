# 设计模式模块

## 模块简介

本模块是 HandMade 手搓学习笔记项目的一部分，专注于学习和实践各种经典设计模式。通过手写实现来深入理解设计模式的原理、应用场景和最佳实践。

## 已实现的设计模式

### 1. 迭代器模式 (Iterator Pattern)

**类型**: 行为型模式

**目的**: 提供一种方法顺序访问一个聚合对象中的各个元素，而又不暴露该对象的内部表示。

**实现包**: `com.richal.learn.iterator`

**核心类**:
- `Iterator<E>` - 迭代器接口
- `Aggregate<E>` - 聚合接口
- `BookShelf` - 具体聚合类（书架）
- `Book` - 元素类（图书）
- `BookShelfIterator` - 具体迭代器（内部类）

**示例代码**:
```java
BookShelf bookShelf = new BookShelf(5);
bookShelf.add(new Book("设计模式", "GoF", 89.0));
bookShelf.add(new Book("Java编程思想", "Bruce Eckel", 108.0));

Iterator<Book> iterator = bookShelf.createIterator();
while (iterator.hasNext()) {
    Book book = iterator.next();
    System.out.println(book);
}
```

**详细文档**: [ITERATOR_PATTERN.md](ITERATOR_PATTERN.md)

---

### 2. 策略模式 (Strategy Pattern)

**类型**: 行为型模式

**目的**: 定义一系列算法，把它们封装起来，并且使它们可以互换。策略模式让算法独立于使用它的客户端而变化。

**实现包**: `com.richal.learn.strategy`

**核心类**:
- `PaymentStrategy` - 策略接口
- `AlipayStrategy` - 支付宝支付策略
- `WeChatPayStrategy` - 微信支付策略
- `CreditCardStrategy` - 银行卡支付策略
- `PaymentContext` - 上下文类
- `Order` - 订单类

**示例代码**:
```java
Order order = new Order();
order.addItem("Java编程思想", 108.00);
order.addItem("设计模式", 89.00);

// 使用支付宝支付
PaymentStrategy alipay = new AlipayStrategy("user@alipay.com", "password");
PaymentContext context = new PaymentContext(alipay);
context.executePayment(order.getTotalAmount());

// 切换到微信支付
context.setStrategy(new WeChatPayStrategy("wx_openid_123", "password"));
context.executePayment(order.getTotalAmount());
```

**详细文档**: [STRATEGY_PATTERN.md](STRATEGY_PATTERN.md)

---

## 计划实现的设计模式

### 创建型模式 (Creational Patterns)

- [ ] **单例模式 (Singleton Pattern)** - 确保一个类只有一个实例
- [ ] **工厂方法模式 (Factory Method Pattern)** - 定义创建对象的接口，让子类决定实例化哪个类
- [ ] **抽象工厂模式 (Abstract Factory Pattern)** - 提供创建一系列相关对象的接口
- [ ] **建造者模式 (Builder Pattern)** - 分步骤构建复杂对象
- [ ] **原型模式 (Prototype Pattern)** - 通过复制现有对象来创建新对象

### 结构型模式 (Structural Patterns)

- [ ] **适配器模式 (Adapter Pattern)** - 将一个类的接口转换成客户希望的另一个接口
- [ ] **桥接模式 (Bridge Pattern)** - 将抽象部分与实现部分分离
- [ ] **组合模式 (Composite Pattern)** - 将对象组合成树形结构
- [ ] **装饰器模式 (Decorator Pattern)** - 动态地给对象添加额外的职责
- [ ] **外观模式 (Facade Pattern)** - 为子系统提供统一的接口
- [ ] **享元模式 (Flyweight Pattern)** - 运用共享技术有效地支持大量细粒度对象
- [ ] **代理模式 (Proxy Pattern)** - 为其他对象提供一种代理以控制对这个对象的访问

### 行为型模式 (Behavioral Patterns)

- [x] **迭代器模式 (Iterator Pattern)** - 顺序访问聚合对象的元素
- [x] **策略模式 (Strategy Pattern)** - 定义一系列算法，并使它们可以互换
- [ ] **观察者模式 (Observer Pattern)** - 定义对象间的一对多依赖关系
- [ ] **模板方法模式 (Template Method Pattern)** - 定义算法骨架，延迟某些步骤到子类
- [ ] **命令模式 (Command Pattern)** - 将请求封装为对象
- [ ] **责任链模式 (Chain of Responsibility Pattern)** - 将请求沿着处理者链传递
- [ ] **状态模式 (State Pattern)** - 允许对象在内部状态改变时改变其行为
- [ ] **访问者模式 (Visitor Pattern)** - 在不改变元素类的前提下定义新操作
- [ ] **中介者模式 (Mediator Pattern)** - 用中介对象封装一系列对象交互
- [ ] **备忘录模式 (Memento Pattern)** - 在不破坏封装的前提下捕获对象的内部状态
- [ ] **解释器模式 (Interpreter Pattern)** - 定义语言的文法，并建立解释器

---

## 项目结构

```
design-patterns/
├── pom.xml                                    # Maven 配置
├── README.md                                  # 本文档
├── DESIGN_PATTERNS_OVERVIEW.md                # 设计模式完整概览
├── ITERATOR_PATTERN.md                        # 迭代器模式详细说明
├── STRATEGY_PATTERN.md                        # 策略模式详细说明
└── src/
    ├── main/java/com/richal/learn/
    │   ├── iterator/                          # 迭代器模式实现
    │   │   ├── Iterator.java                  # 迭代器接口
    │   │   ├── Aggregate.java                 # 聚合接口
    │   │   ├── BookShelf.java                 # 具体聚合类
    │   │   └── Book.java                      # 元素类
    │   └── strategy/                          # 策略模式实现
    │       ├── PaymentStrategy.java           # 策略接口
    │       ├── AlipayStrategy.java            # 支付宝策略
    │       ├── WeChatPayStrategy.java         # 微信支付策略
    │       ├── CreditCardStrategy.java        # 银行卡策略
    │       ├── PaymentContext.java            # 上下文类
    │       └── Order.java                     # 订单类
    └── test/java/com/richal/learn/
        ├── iterator/                          # 迭代器模式测试
        │   └── IteratorPatternTest.java       # 测试类
        └── strategy/                          # 策略模式测试
            └── StrategyPatternTest.java       # 测试类
```

---

## 学习资源

### 推荐书籍

1. **《设计模式：可复用面向对象软件的基础》** - GoF (Gang of Four)
   - 设计模式的经典之作，必读书籍

2. **《Head First 设计模式》** - Eric Freeman & Elisabeth Robson
   - 通俗易懂，适合初学者

3. **《设计模式之禅》** - 秦小波
   - 中文原创，结合实际案例讲解

### 在线资源

- [Refactoring.Guru](https://refactoring.guru/design-patterns) - 设计模式可视化教程
- [SourceMaking](https://sourcemaking.com/design-patterns) - 设计模式详解
- [Java Design Patterns](https://java-design-patterns.com/) - Java 设计模式实现

---

## 设计原则

在学习设计模式的同时，也要理解支撑这些模式的设计原则：

### SOLID 原则

1. **单一职责原则 (SRP)** - Single Responsibility Principle
   - 一个类应该只有一个引起它变化的原因

2. **开闭原则 (OCP)** - Open-Closed Principle
   - 软件实体应该对扩展开放，对修改关闭

3. **里氏替换原则 (LSP)** - Liskov Substitution Principle
   - 子类对象应该能够替换父类对象

4. **接口隔离原则 (ISP)** - Interface Segregation Principle
   - 客户端不应该依赖它不需要的接口

5. **依赖倒置原则 (DIP)** - Dependency Inversion Principle
   - 高层模块不应该依赖低层模块，两者都应该依赖抽象

### 其他重要原则

- **迪米特法则 (LoD)** - Law of Demeter
  - 最少知识原则，一个对象应该对其他对象有最少的了解

- **合成复用原则 (CRP)** - Composite Reuse Principle
  - 优先使用对象组合，而不是继承

---

## 运行测试

```bash
# 编译项目
mvn clean compile

# 运行所有测试
mvn test

# 运行特定模式的测试
mvn test -Dtest=IteratorPatternTest

# 运行演示程序
mvn exec:java -Dexec.mainClass="com.richal.learn.iterator.IteratorPatternTest"
```

---

## 贡献指南

每个设计模式的实现应该包含：

1. **接口定义** - 清晰的接口设计
2. **具体实现** - 完整的实现代码
3. **测试用例** - 全面的单元测试
4. **文档说明** - 详细的使用文档和 UML 图
5. **示例代码** - 实际应用场景的示例

---

## 作者

**Richal** - HandMade 手搓学习笔记项目

---

## 许可证

本项目仅用于学习目的。

---

## 更新日志

### 2026-01-19
- ✅ 创建设计模式模块
- ✅ 实现迭代器模式
- ✅ 实现策略模式
- ✅ 添加完整的测试用例
- ✅ 编写详细的文档和 UML 类图（Mermaid + ASCII）
