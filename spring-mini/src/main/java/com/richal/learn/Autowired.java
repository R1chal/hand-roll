package com.richal.learn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 💉 @Autowired 注解 - 依赖注入核心
 * 
 * 📋 作用：标记字段以自动注入Bean实例
 * 🔍 流程：
 * 1. 扫描标记字段
 * 2. 按类型查找匹配Bean
 * 3. 反射注入Bean实例
 * 
 * 💡 Spring框架设计思想：
 * - 实现控制反转(IoC)：类不负责创建依赖，由容器注入
 * - 支持类型匹配：自动查找匹配的Bean类型
 * - 简化对象关系：减少硬编码的依赖创建代码
 * - 提高可测试性：可以轻松替换依赖实现
 * 
 * 🔄 Spring Bean生命周期中的依赖注入时机：
 * - 实例化后：Bean对象已创建但字段为null
 * - 依赖注入前：扫描所有@Autowired字段
 * - 依赖注入中：按类型查找并反射设置字段值
 * - 依赖注入后：所有依赖字段已正确设置
 * 
 * 🎯 核心设计理念：
 * - 控制反转：将依赖的创建和管理权交给容器
 * - 自动装配：根据类型自动匹配和注入依赖
 * - 松耦合：类之间通过接口或抽象类型交互，不依赖具体实现
 * - 生命周期管理：容器负责管理依赖的创建、注入和销毁
 */
@Target(ElementType.FIELD)          // 📍 限定注解只能用在字段上
@Retention(RetentionPolicy.RUNTIME) // 🕐 运行时保留，支持反射进行依赖注入
public @interface Autowired {
    
    // 💭 设计说明：当前版本为简化实现，无额外配置参数
    
}
