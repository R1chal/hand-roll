package com.richal.learn;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 📋 BeanDefinition - Bean的元数据定义类
 * 
 * 🎯 核心作用：
 * 这个类是Spring IOC容器的核心概念之一，用于存储Bean的所有元数据信息。
 * 它就像是Bean的"身份证"，记录了创建和管理Bean实例所需的所有信息。
 * 
 * 🔍 为什么需要BeanDefinition？
 * 1. 【分离关注点】：将Bean的元数据（如何创建）与Bean实例（实际对象）分离
 * 2. 【延迟实例化】：先收集所有Bean的定义信息，再统一创建实例
 * 3. 【灵活管理】：可以在创建实例前修改Bean的配置信息
 * 4. 【循环依赖处理】：通过元数据分析依赖关系，解决循环依赖问题
 * 5. 【性能优化】：避免重复的反射操作，缓存反射信息
 * 
 * 🏗️ 设计模式：
 * - 建造者模式：通过BeanDefinition构建Bean实例
 * - 元数据模式：将对象的描述信息与对象本身分离
 * - 工厂模式：BeanDefinition是创建Bean的"配方"
 * 
 * 🔄 在Spring生命周期中的位置：
 * 1. 📤 扫描阶段：扫描@Component注解的类，创建BeanDefinition
 * 2. 📋 注册阶段：将BeanDefinition注册到容器的beanDefinitionMap中
 * 3. 🏭 实例化阶段：根据BeanDefinition创建Bean实例
 * 4. 💉 注入阶段：根据BeanDefinition的信息进行依赖注入
 * 5. 🚀 初始化阶段：根据BeanDefinition执行@PostConstruct方法
 * 
 * 💡 与实际Spring Framework的对比：
 * - 实际Spring的BeanDefinition是一个接口，有多个实现类
 * - 包含更多属性：scope、lazy-init、depends-on、init-method等
 * - 支持更复杂的创建方式：工厂方法、配置类等
 * - 当前实现是简化版，只包含基本的创建信息
 */
public class BeanDefinition {

    /**
     * 🏷️ Bean的名称标识
     * 
     * 📋 作用说明：
     * - Bean在IOC容器中的唯一标识符
     * - 通过这个名称可以从容器中获取Bean实例
     * - 如果@Component没有指定name，默认使用类名首字母小写
     * 
     * 💡 命名规则：
     * - UserService → userService (默认)
     * - @Component(name="myService") → myService (指定)
     */
    private String name;

    /**
     * 🏗️ 构造器对象
     * 
     * 📋 作用说明：
     * - 存储用于创建Bean实例的构造器
     * - 当前实现只支持无参构造器
     * - 在实例化阶段通过constructor.newInstance()创建Bean
     * 
     * 🔍 为什么缓存Constructor？
     * - 避免每次创建Bean时都进行反射查找
     * - 反射操作相对耗时，缓存可以提高性能
     * - 确保使用一致的构造器创建实例
     */
    private Constructor<?> constructor;

    /**
     * 🚀 PostConstruct初始化方法
     * 
     * 📋 作用说明：
     * - 存储@PostConstruct标记的初始化方法
     * - 在Bean实例创建和依赖注入完成后调用
     * - 如果没有@PostConstruct方法，则为null
     * 
     * 🔄 执行时机：
     * Constructor创建实例 → 依赖注入 → PostConstruct方法执行
     */
    private Method postConstructMethod;

    /**
     * 🏗️ 构造器 - 基于类型创建BeanDefinition
     * 
     * 📋 功能说明：
     * 根据传入的Class对象，提取并初始化Bean的所有元数据信息
     * 
     * 🔍 处理流程：
     * 1. 解析@Component注解，确定Bean名称
     * 2. 获取无参构造器，用于后续实例化
     * 3. 查找@PostConstruct方法，用于初始化回调
     * 
     * @param type 需要管理的Bean的Class对象
     * @throws RuntimeException 如果类没有无参构造器或反射操作失败
     */
    public BeanDefinition(Class<?> type) {
        // 🔍 步骤1：解析@Component注解，确定Bean名称
        Component declaredAnnotation = type.getDeclaredAnnotation(Component.class);
        // 📝 命名策略：如果指定了name则使用指定值，否则使用类名首字母小写
        this.name = declaredAnnotation.name().isEmpty() 
            ? type.getSimpleName() 
            : declaredAnnotation.name();
            
        try {
            // 🏗️ 步骤2：获取无参构造器
            // 📝 当前实现限制：只支持无参构造器，简化了Bean的创建逻辑
            this.constructor = type.getConstructor();
            
            // 🚀 步骤3：查找@PostConstruct标记的初始化方法
            // 🔧 修复：应该从methods中查找，而不是constructors
            this.postConstructMethod = Arrays.stream(type.getDeclaredMethods())  // ✅ 修复：使用getDeclaredMethods
                .filter(m -> m.isAnnotationPresent(PostConstruct.class))
                .findFirst()
                .orElse(null);  // 如果没有找到@PostConstruct方法，则为null
                
        } catch (NoSuchMethodException e) {
            // 🚨 异常处理：如果类没有无参构造器，创建BeanDefinition失败
            throw new RuntimeException("类 " + type.getName() + " 没有无参构造器，无法创建Bean", e);
        }
    }

    /**
     * 🔧 获取构造器
     * 
     * @return 用于创建Bean实例的构造器对象
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * 🏷️ 获取Bean名称
     * 
     * @return Bean在IOC容器中的唯一标识名称
     */
    public String getName() {
        return name;
    }

    /**
     * 🚀 获取PostConstruct方法
     * 
     * @return @PostConstruct标记的初始化方法，如果没有则返回null
     */
    public Method getPostConstructMethod() {
        return postConstructMethod;
    }

}
