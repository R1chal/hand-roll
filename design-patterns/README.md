# 设计模式学习模块

> 本模块是 HandMade 手搓学习笔记项目的一部分，专注于学习和实践各种经典设计模式。

## 快速开始

### 迭代器模式示例

```java
// 创建书架
BookShelf bookShelf = new BookShelf(5);

// 添加书籍
bookShelf.add(new Book("设计模式", "GoF", 89.0));
bookShelf.add(new Book("Java编程思想", "Bruce Eckel", 108.0));
bookShelf.add(new Book("深入理解Java虚拟机", "周志明", 79.0));

// 使用迭代器遍历
Iterator<Book> iterator = bookShelf.createIterator();
while (iterator.hasNext()) {
    Book book = iterator.next();
    System.out.println(book);
}
```

### 策略模式示例

```java
// 创建订单
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

## 已实现的模式

- ✅ **迭代器模式 (Iterator Pattern)** - [详细文档](ITERATOR_PATTERN.md)
- ✅ **策略模式 (Strategy Pattern)** - [详细文档](STRATEGY_PATTERN.md)

## 文档导航

- 📖 [设计模式完整概览](DESIGN_PATTERNS_OVERVIEW.md) - 查看所有设计模式的学习计划
- 📊 [迭代器模式详解](ITERATOR_PATTERN.md) - 包含 UML 类图和序列图

## 运行测试

```bash
# 运行所有测试
mvn test -pl design-patterns

# 运行迭代器模式演示
cd design-patterns
mvn exec:java -Dexec.mainClass="com.richal.learn.iterator.IteratorPatternTest"
```

## 项目结构

```
design-patterns/
├── src/main/java/com/richal/learn/
│   ├── iterator/              # 迭代器模式实现
│   └── strategy/              # 策略模式实现
└── src/test/java/com/richal/learn/
    ├── iterator/              # 迭代器模式测试
    └── strategy/              # 策略模式测试
```

---

**作者**: Richal | **项目**: HandMade 手搓学习笔记

