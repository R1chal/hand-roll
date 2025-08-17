package com.richal.learn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 🏷️ @Component 注解 - Spring IOC容器核心标记
 * 
 * 📋 作用：标记类为Spring管理的Bean
 * 🔍 流程：
 * 1. 标记类
 * 2. Spring扫描并注册
 * 3. 加入IOC容器
 * 
 * 💡 Spring框架设计思想：
 * - 通过注解声明式地标记需要管理的类
 * - 遵循"约定优于配置"原则，减少XML配置
 * - 支持自定义Bean名称，提供灵活性
 * 
 * 🔄 Spring容器启动流程中的角色：
 * - 包扫描阶段：识别所有@Component标记的类
 * - Bean定义阶段：将类信息封装为BeanDefinition
 * - Bean创建阶段：实例化并管理Bean生命周期
 * 
 * 🎯 核心设计理念：
 * - 声明式编程：通过注解声明意图，而不是命令式配置
 * - 自动发现：容器自动扫描和识别需要管理的组件
 * - 约定优于配置：默认规则简化配置，特殊需求可自定义
 * - 元数据驱动：注解提供元数据，容器根据元数据管理Bean
 */
@Target(ElementType.TYPE)           // 📍 限定注解只能用在类型声明上
@Retention(RetentionPolicy.RUNTIME) // 🕐 运行时保留，确保反射能够获取到
public @interface Component {

    /**
     * 🏷️ Bean名称标识
     * 📋 作用：指定Bean在IOC容器中的名称
     * 🔍 规则：默认类名首字母小写，或指定名称
     * @return Bean名称，默认空字符串（使用默认规则）
     */
    String name() default "";

}
