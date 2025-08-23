package com.richal.learn;

/**
 * 动态代理的处理器接口
 * 
 * 这是整个动态代理系统的核心接口，定义了如何生成代理类的方法实现。
 * 通过实现这个接口，可以定义不同的代理行为，实现AOP、装饰器等设计模式。
 * 
 * 核心流程：
 * 1. functionBody(): 生成代理类中方法的具体实现代码
 * 2. setProxy(): 在代理对象创建后，进行必要的初始化设置
 * 
 * 设计模式：策略模式 + 模板方法模式
 * 应用场景：日志记录、性能监控、事务管理、缓存、权限控制等
 */
public interface MyHandler {
    
    /**
     * 生成代理类中方法的具体实现代码
     * 
     * 这个方法返回一个字符串，包含Java代码。系统会：
     * 1. 调用此方法获取方法实现代码
     * 2. 将返回的代码字符串插入到生成的代理类中
     * 3. 编译生成的代理类
     * 
     * @param methodName 需要实现的方法名
     * @return 包含Java代码的字符串，这些代码将作为代理类中方法的实现
     * 
     * 示例：
     * - 简单打印：return "System.out.println(\"" + methodName + "\");";
     * - 日志记录：return "System.out.println(\"before\"); this.myInterface." + methodName + "(); System.out.println(\"after\");";
     * - 性能监控：return "long start = System.currentTimeMillis(); this.myInterface." + methodName + "(); System.out.println(\"耗时: \" + (System.currentTimeMillis() - start));";
     */
    String functionBody(String methodName);

    /**
     * 设置代理对象，进行必要的初始化
     * 
     * 这个方法在代理对象创建后被调用，用于：
     * 1. 注入目标对象到代理对象中
     * 2. 设置必要的字段值
     * 3. 建立代理对象与目标对象的关联
     * 
     * 默认实现为空，简单处理器可以不需要特殊处理。
     * 复杂的处理器（如LogHandler）需要重写此方法来实现特定的初始化逻辑。
     * 
     * @param proxy 生成的代理对象实例
     * 
     * 使用场景：
     * - 日志处理器：注入目标对象，实现方法调用前后的日志记录
     * - 事务处理器：设置事务上下文
     * - 缓存处理器：设置缓存管理器
     */
    default void setProxy(MyInterface proxy) {
        // 默认实现为空，简单处理器不需要特殊处理
    }
}
