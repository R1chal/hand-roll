package com.richal.learn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 💉 @Autowired 注解 - 依赖注入(DI)的核心注解
 * 
 * 📚 核心概念：
 * 1. 自动装配：Spring容器自动为标记的字段注入对应的Bean实例
 * 2. 依赖注入：对象不需要自己创建依赖，由容器负责注入
 * 3. 控制反转：对象的创建和依赖关系的管理权交给Spring容器
 * 
 * 🔍 工作原理：
 * 1. Spring容器扫描到@Autowired标记的字段
 * 2. 根据字段类型在IOC容器中查找匹配的Bean
 * 3. 使用反射将找到的Bean实例注入到目标字段中
 * 4. 如果找不到匹配的Bean，抛出异常（当前实现）
 * 
 * 🎯 装配策略（当前简化实现）：
 * - 按类型装配(byType)：根据字段的类型查找匹配的Bean
 * - 实际Spring还支持按名称装配(byName)、按构造器装配等
 * 
 * 💡 使用示例：
 * ```java
 * @Component
 * public class UserService {
 *     @Autowired
 *     private UserRepository userRepository; // 自动注入UserRepository实例
 * }
 * ```
 * 
 * 🚨 注意事项：
 * - 字段必须是private，体现封装性
 * - 被注入的对象必须也是Spring管理的Bean（标记@Component）
 * - 当前实现不支持循环依赖的处理
 * 
 * 🔗 与其他注解的关系：
 * - @Component：标记类为Spring管理的Bean
 * - @Autowired：标记字段需要被自动注入
 * - @PostConstruct：在依赖注入完成后执行初始化逻辑
 */
@Target(ElementType.FIELD)          // 📍 限定注解只能用在字段上
@Retention(RetentionPolicy.RUNTIME) // 🕐 运行时保留，支持反射进行依赖注入
public @interface Autowired {
    
    // 💭 设计说明：
    // 当前版本为简化实现，没有额外配置参数
    // 实际Spring的@Autowired还支持：
    // - required属性：是否必须注入，默认true
    // - qualifier：指定特定的Bean名称进行注入
    
}
