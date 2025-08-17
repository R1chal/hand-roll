package com.richal.learn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 🚀 @PostConstruct 注解 - Bean初始化回调
 * 
 * 📋 作用：标记方法在Bean初始化后执行
 * 🔍 流程：
 * 1. 实例化Bean
 * 2. 注入依赖
 * 3. 执行@PostConstruct方法
 * 
 * 💡 Spring框架设计思想：
 * - 生命周期管理：提供Bean生命周期的钩子点
 * - 初始化顺序：确保依赖注入完成后再执行初始化逻辑
 * - 解耦设计：业务逻辑与框架初始化逻辑分离
 * - 标准化：遵循JSR-250规范，提供统一的初始化方式
 * 
 * 🔄 Spring Bean生命周期中的初始化时机：
 * - 实例化阶段：通过构造器创建Bean对象
 * - 依赖注入阶段：注入所有@Autowired字段
 * - 初始化阶段：执行@PostConstruct方法
 * - 后处理阶段：执行BeanPostProcessor的after方法
 * - 完成阶段：Bean完全初始化，可正常使用
 * 
 * 🎯 核心设计理念：
 * - 生命周期钩子：在Bean生命周期的关键节点提供回调机会
 * - 初始化顺序保证：确保依赖注入完成后再执行初始化逻辑
 * - 业务逻辑分离：将初始化逻辑与框架逻辑分离，提高可维护性
 * - 标准化接口：遵循JSR-250规范，与其他框架兼容
 */
@Target(ElementType.METHOD)         // 📍 限定注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME) // 🕐 运行时保留，支持反射调用
public @interface PostConstruct {
    
    // 💭 设计说明：这是一个标记注解，无需额外配置参数
    
}
