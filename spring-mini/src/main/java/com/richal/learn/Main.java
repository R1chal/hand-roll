package com.richal.learn;

import java.io.IOException;

/**
 * 🚀 Main - MiniSpring应用程序启动入口
 * 
 * 📚 程序说明：
 * 这是MiniSpring框架的测试和演示程序，展示了如何使用自定义的IOC容器
 * 
 * 🔄 执行流程：
 * 1. 启动ApplicationContext容器
 * 2. 指定包扫描路径"com.richal.learn"
 * 3. 容器自动扫描并初始化所有@Component标记的Bean
 * 4. 执行Bean的生命周期管理（实例化 → 依赖注入 → 初始化回调）
 * 
 * 📦 扫描范围：
 * "com.richal.learn"包及其子包，包含：
 * - com.richal.learn.sub.Dog：简单Bean，展示基本组件注册
 * - com.richal.learn.sub.Cat：复杂Bean，展示依赖注入和生命周期管理
 * 
 * 🎯 预期输出：
 * 程序运行时应该在控制台看到：
 * "Cat init" - 表示Cat Bean的@PostConstruct方法被成功调用
 * 
 * 💡 扩展示例：
 * 可以在main方法中添加更多代码来验证容器功能：
 * 
 * ```java
 * public static void main(String[] args) throws IOException {
 *     // 启动容器
 *     ApplicationContext context = new ApplicationContext("com.richal.learn");
 *     
 *     // 测试Bean获取
 *     Cat cat = context.getBean(Cat.class);
 *     Dog dog = (Dog) context.getBean("myDog");
 *     
 *     // 验证单例模式
 *     Cat cat2 = context.getBean(Cat.class);
 *     System.out.println("单例验证: " + (cat == cat2)); // true
 *     
 *     // 测试业务方法
 *     System.out.println(cat.playWithDog());
 *     
 *     // 验证依赖注入（当前版本dog为null）
 *     System.out.println("Dog注入状态: " + (cat.getDog() != null));
 * }
 * ```
 * 
 * 🚨 当前版本特点：
 * - ✅ 包扫描功能完整
 * - ✅ Bean实例化和注册功能完整  
 * - ✅ @PostConstruct生命周期回调功能完整
 * - ❌ @Autowired依赖注入功能待实现
 * - ❌ 循环依赖处理功能待实现
 * 
 * 🎯 学习价值：
 * 通过这个简单的启动类，可以理解：
 * 1. Spring IOC容器的启动过程
 * 2. 包扫描和组件发现机制
 * 3. Bean生命周期管理的基本原理
 * 4. 依赖注入框架的设计思路
 */
public class Main {
    
    /**
     * 🎯 应用程序主入口方法
     * 
     * 📋 功能说明：
     * 创建并启动ApplicationContext容器，触发完整的Spring IOC初始化流程
     * 
     * 🔄 执行步骤：
     * 1. new ApplicationContext("com.richal.learn") 
     *    → 触发容器构造器
     * 2. initContext(packageName) 
     *    → 启动容器初始化流程
     * 3. scanPackage() 
     *    → 扫描指定包下的所有class文件
     * 4. scanCreate() 
     *    → 过滤出@Component标记的类
     * 5. wrapper() 
     *    → 将Class封装为BeanDefinition
     * 6. createBean() 
     *    → 实例化Bean并执行初始化
     * 
     * 💡 包名说明：
     * "com.richal.learn" 是基础包名，容器会扫描该包及其所有子包
     * 
     * @param args 命令行参数（当前未使用）
     * @throws IOException 如果包扫描过程中发生IO异常
     */
    public static void main(String[] args) throws IOException {
        // 🚀 启动Spring IOC容器，开始Bean管理之旅
        new ApplicationContext("com.richal.learn");
        
        // 🎉 容器启动完成！
        // 此时所有@Component标记的Bean都已经：
        // 1. 被扫描发现
        // 2. 创建了BeanDefinition
        // 3. 实例化为对象
        // 4. 执行了@PostConstruct初始化方法
        // 5. 注册到IOC容器中，可以通过getBean()获取
        
        // 💭 可以在这里添加更多测试代码来验证容器功能...
    }
}