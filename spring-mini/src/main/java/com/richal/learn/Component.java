package com.richal.learn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 🏷️ @Component 注解 - Spring IOC容器的核心标记注解
 * 
 * 📚 作用说明：
 * 1. 标记一个类为Spring管理的Bean组件
 * 2. 告诉Spring容器："这个类需要被实例化并加入到IOC容器中"
 * 3. 类似于Spring Framework中的 @Component、@Service、@Repository 等注解
 * 
 * 🔍 设计原理：
 * - 使用注解驱动的方式替代XML配置
 * - 通过反射机制在运行时识别哪些类需要被Spring管理
 * - 实现控制反转(IOC)和依赖注入(DI)的基础
 * 
 * 💡 使用示例：
 * @Component                    // 使用默认名称（类名首字母小写）
 * @Component(name = "myBean")   // 指定自定义Bean名称
 * 
 * 🎯 注解元信息：
 * - @Target(ElementType.TYPE)：只能用在类、接口、枚举上
 * - @Retention(RetentionPolicy.RUNTIME)：运行时保留，支持反射访问
 */
@Target(ElementType.TYPE)           // 📍 限定注解只能用在类型声明上
@Retention(RetentionPolicy.RUNTIME) // 🕐 运行时保留，确保反射能够获取到
public @interface Component {

    /**
     * 🏷️ Bean的名称标识
     * 
     * 📋 功能说明：
     * - 如果不指定name，默认使用类名首字母小写作为Bean名称
     * - 如果指定name，则使用指定的名称作为Bean在IOC容器中的唯一标识
     * - 通过ApplicationContext.getBean(name)可以根据名称获取Bean实例
     * 
     * 🚨 注意事项：
     * - Bean名称在同一个容器中必须唯一，重复会抛出异常
     * - 建议使用有意义的名称，便于调试和维护
     * 
     * @return Bean的名称，默认为空字符串（使用默认命名规则）
     */
    String name() default "";

}
