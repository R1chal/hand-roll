package com.richal.learn.sub;

import com.richal.learn.Component;

/**
 * 🐕 Dog - 简单Bean示例类
 * 
 * 📚 示例说明：
 * 这是一个最简单的Spring Bean示例，展示了：
 * 1. 使用@Component注解标记类为Spring管理的组件
 * 2. 自定义Bean名称的用法
 * 3. 无依赖的独立Bean
 * 
 * 🏷️ Bean配置：
 * - 注解：@Component(name = "myDog")
 * - Bean名称：myDog（自定义名称，而不是默认的"dog"）
 * - 依赖：无（独立Bean）
 * - 生命周期：无特殊配置（默认生命周期）
 * 
 * 🎯 在容器中的使用：
 * ```java
 * ApplicationContext context = new ApplicationContext("com.richal.learn");
 * 
 * // 通过自定义名称获取
 * Dog dog1 = (Dog) context.getBean("myDog");
 * 
 * // 通过类型获取
 * Dog dog2 = context.getBean(Dog.class);
 * 
 * // dog1 和 dog2 是同一个实例（单例模式）
 * System.out.println(dog1 == dog2); // true
 * ```
 * 
 * 💡 设计目的：
 * - 作为Cat类的依赖，演示依赖注入
 * - 展示最简单的Bean配置方式
 * - 验证容器的基本Bean管理功能
 */
@Component(name = "myDog")  // 🏷️ 指定Bean名称为"myDog"而不是默认的"dog"
public class Dog {
    
    // 📝 这是一个最简单的Bean实现：
    // - 只有@Component注解
    // - 没有字段、方法或构造器
    // - 依赖容器提供的默认无参构造器
    // - 没有初始化逻辑或依赖注入
    
}
