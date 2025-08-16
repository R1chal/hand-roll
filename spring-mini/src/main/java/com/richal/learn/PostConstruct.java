package com.richal.learn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 🚀 @PostConstruct 注解 - Bean生命周期管理的初始化回调
 * 
 * 📚 核心概念：
 * 1. 生命周期回调：在Bean的特定生命周期阶段自动执行指定方法
 * 2. 初始化逻辑：执行Bean创建后的自定义初始化操作
 * 3. 钩子方法：Spring容器在合适的时机自动调用的方法
 * 
 * 🔄 Bean生命周期中的执行时机：
 * 1. 📦 实例化：通过反射调用构造器创建Bean实例
 * 2. 💉 依赖注入：为@Autowired字段注入依赖对象
 * 3. 🚀 初始化回调：执行@PostConstruct标记的方法 ← 当前阶段
 * 4. ✅ Bean就绪：Bean完全初始化，可以被其他组件使用
 * 5. 💀 销毁：容器关闭时执行@PreDestroy方法（当前未实现）
 * 
 * 🎯 使用场景：
 * - 初始化资源：打开文件、建立网络连接、初始化缓存等
 * - 验证配置：检查注入的依赖是否正确配置
 * - 启动后台任务：启动定时器、监听器等
 * - 数据预加载：从数据库加载初始数据
 * 
 * 💡 使用示例：
 * ```java
 * @Component
 * public class DatabaseService {
 *     @Autowired
 *     private DataSource dataSource;
 *     
 *     @PostConstruct
 *     public void initConnection() {
 *         // 在依赖注入完成后，初始化数据库连接池
 *         System.out.println("数据库连接池初始化完成");
 *     }
 * }
 * ```
 * 
 * 🚨 重要约束：
 * - 方法必须是无参数的
 * - 方法不能是static的
 * - 方法可以是任意访问修饰符（public、private等）
 * - 一个类中只能有一个@PostConstruct方法（推荐）
 * - 如果初始化方法抛出异常，Bean创建失败
 * 
 * 🔍 与构造器的区别：
 * - 构造器执行时，依赖注入还没有完成
 * - @PostConstruct执行时，所有@Autowired字段都已经注入完成
 * - 可以在@PostConstruct中安全地使用注入的依赖
 * 
 * 📋 执行顺序保证：
 * Constructor → @Autowired字段注入 → @PostConstruct方法执行
 */
@Target(ElementType.METHOD)         // 📍 限定注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME) // 🕐 运行时保留，支持反射调用
public @interface PostConstruct {
    
    // 💭 设计说明：
    // 这是一个标记注解，不需要额外的配置参数
    // 其作用类似于Spring Framework中的JSR-250标准注解
    // 
    // 🔄 生命周期对比：
    // JSR-250标准定义了完整的生命周期注解：
    // - @PostConstruct：初始化后执行
    // - @PreDestroy：销毁前执行（当前MiniSpring未实现）
    
}
