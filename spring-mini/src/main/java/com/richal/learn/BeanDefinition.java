package com.richal.learn;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 📋 BeanDefinition - Bean元数据定义
 * 
 * 🎯 作用：存储Bean创建和管理所需信息
 * 🔍 功能：
 * 1. 缓存Bean名称、构造器、初始化方法
 * 2. 支持延迟实例化与性能优化
 * 
 * 🔄 流程：扫描类 -> 创建定义 -> 注册容器 -> 按需创建Bean
 * 
 * 💡 Spring框架设计思想：
 * - 元数据驱动：将类的信息抽象为BeanDefinition，支持配置化
 * - 延迟加载：先注册定义，按需创建实例，提高启动性能
 * - 反射优化：缓存构造器、方法等反射信息，避免重复解析
 * - 扩展性：支持不同的Bean类型和创建策略
 * 
 * 🔄 Spring容器设计中的元数据管理：
 * - 扫描阶段：发现@Component类，立即创建BeanDefinition
 * - 注册阶段：将BeanDefinition存储到beanDefinitionMap
 * - 创建阶段：按需使用BeanDefinition创建Bean实例
 * - 缓存策略：避免重复解析类信息，提高性能
 * 
 * 🎯 核心设计理念：
 * - 元数据抽象：将类的结构信息抽象为可操作的数据结构
 * - 配置与实现分离：BeanDefinition存储配置，运行时动态创建Bean
 * - 性能优化：缓存反射信息，避免重复解析
 * - 扩展性设计：支持不同的Bean创建策略和生命周期管理
 */
public class BeanDefinition {

    /**
     * 🏷️ Bean名称标识
     * 📋 作用：Bean在IOC容器中的唯一标识
     * 🔍 规则：默认类名首字母小写，或@Component指定名称
     */
    private String name;

    /**
     * 🏗️ 构造器对象
     * 📋 作用：用于创建Bean实例
     * 🔍 特点：缓存构造器避免重复反射，支持无参构造
     */
    private Constructor<?> constructor;

    /**
     * 🚀 PostConstruct初始化方法
     * 📋 作用：存储@PostConstruct标记的方法
     * 🔍 时机：实例创建和依赖注入后调用
     */
    private Method postConstructMethod;

    private List<Field> autowiredFields;

    private Class<?> beantype;

    /**
     * 🏗️ 构造器 - 基于类型创建BeanDefinition
     * 📋 功能：提取Bean元数据信息
     * 🔍 流程：
     * 1. 解析@Component注解定名称
     * 2. 获取无参构造器
     * 3. 查找@PostConstruct方法
     * 4. 查找@Autowired字段
     * @param type Bean的Class对象
     * @throws RuntimeException 无无参构造器抛出异常
     * 
     * 💡 设计要点：
     * - 元数据提取：在Bean创建前完成所有信息的解析
     * - 构造器限制：当前只支持无参构造器，简化了Bean创建逻辑
     * - 注解扫描：通过反射获取所有相关注解信息
     * - 异常处理：明确的错误提示，便于调试
     * 
     * 🔄 Spring元数据解析策略：
     * - 注解优先：优先使用@Component(name)指定的名称
     * - 默认命名：未指定时使用类名首字母小写
     * - 反射缓存：一次性解析所有反射信息，避免重复计算
     * - 异常处理：明确的错误信息，便于快速定位问题
     */
    public BeanDefinition(Class<?> type) {
        this.beantype = type;
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

            this.autowiredFields = Arrays.stream(type.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(Autowired.class))
                    .toList();

        } catch (NoSuchMethodException e) {
            // 🚨 异常处理：如果类没有无参构造器，创建BeanDefinition失败
            throw new RuntimeException("类 " + type.getName() + " 没有无参构造器，无法创建Bean", e);
        }
    }

    /**
     * 🔧 获取构造器
     * @return 用于创建Bean实例的构造器对象
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * 🏷️ 获取Bean名称
     * @return Bean在IOC容器中的唯一标识名称
     */
    public String getName() {
        return name;
    }

    /**
     * 🚀 获取PostConstruct方法
     * @return @PostConstruct标记的初始化方法，如果没有则返回null
     */
    public Method getPostConstructMethod() {
        return postConstructMethod;
    }
    public List<Field> getAutowiredFields() {
        return autowiredFields;
    }

    public Class<?> getBeantype() {
        return beantype;
    }


}
