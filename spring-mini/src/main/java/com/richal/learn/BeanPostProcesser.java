package com.richal.learn;

/**
 * 🛠️ BeanPostProcesser - Bean后处理器接口
 * 
 * 📋 作用：提供Bean初始化前后的自定义处理
 * 🔍 流程：
 * 1. beforeInitializeBean - 初始化前处理
 * 2. afterInitializeBean - 初始化后处理
 * 
 * 💡 Spring框架设计思想：
 * - 扩展点设计：提供Bean生命周期的扩展钩子
 * - 责任链模式：支持多个后处理器串联执行
 * - 横切关注点：可以在不修改Bean代码的情况下添加通用逻辑
 * - 插件化架构：支持动态添加和移除后处理器
 * 
 * 🔄 Spring Bean生命周期中的后处理时机：
 * - 实例化后：Bean对象已创建，但字段未注入
 * - 依赖注入前：beforeInitializeBean被调用
 * - 依赖注入后：@PostConstruct方法执行前
 * - 初始化后：afterInitializeBean被调用
 * - 完成阶段：Bean完全初始化，可正常使用
 * 
 * 🎯 实际应用场景：
 * - 代理创建：AOP代理、事务代理等
 * - 属性设置：从配置文件读取属性值
 * - 验证检查：Bean配置的合法性验证
 * - 监控统计：Bean创建耗时、依赖关系等
 */
public interface BeanPostProcesser {

    /**
     * 🔄 初始化前处理
     * @param bean Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
    default Object beforeInitializeBean(Object bean, String beanName){
        return bean;
    }
    
    /**
     * 🔄 初始化后处理
     * @param bean Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
    default Object afterInitializeBean(Object bean, String beanName){
        return bean;
    }
}
