package com.richal.learn.sub;

import com.richal.learn.Component;

/**
 * 🐕 Dog - 简单Bean示例
 * 
 * 📋 作用：展示基本Spring Bean配置
 * 🔍 流程：
 * 1. @Component标记为Spring组件
 * 2. 指定自定义Bean名称
 * 3. 注册到IOC容器
 * 
 * 💡 Spring Bean命名策略：
 * - 默认命名：类名首字母小写（如：Dog -> dog）
 * - 自定义命名：通过@Component(name="xxx")指定
 * - 命名冲突：相同名称的Bean会抛出异常
 * - 命名规范：建议使用有意义的名称，便于维护
 * 
 * 🔄 Spring容器中的Bean管理：
 * - 名称解析：使用@Component(name="myDog")指定Bean名称
 * - 唯一性保证：容器中每个Bean名称都是唯一的
 * - 类型匹配：支持通过Dog类型或"myDog"名称获取Bean
 * - 延迟创建：只有在需要时才创建Bean实例
 */
@Component(name = "myDog")  // 🏷️ 指定Bean名称为"myDog"而不是默认的"dog"
public class Dog {
    
    // 📝 简单Bean实现：无字段、方法，仅使用默认构造器
    
}
