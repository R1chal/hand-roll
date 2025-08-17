package com.richal.learn;

/**
 * 🛠️ MyBeanPostProcesser - 自定义Bean后处理器
 * 
 * 📋 作用：在Bean初始化前后执行自定义逻辑
 * 🔍 流程：
 * 1. 初始化前处理（未实现）
 * 2. 初始化后处理（打印初始化完成信息）
 * 
 * 💡 实际应用场景：
 * - 日志记录：记录Bean的创建和销毁过程
 * - 性能监控：统计Bean初始化耗时
 * - 安全检查：验证Bean的配置和依赖
 * - 缓存预热：在Bean初始化后预加载数据
 * 
 * 🔄 Spring容器中的执行顺序：
 * - 优先创建：BeanPostProcessor优先于其他Bean创建
 * - 责任链执行：多个后处理器按注册顺序依次执行
 * - 生命周期钩子：在Bean生命周期的关键节点执行
 * - 扩展点：不修改Bean代码即可添加通用逻辑
 */
@Component
public class MyBeanPostProcesser implements BeanPostProcesser{

    @Override
    /**
     * 🔄 初始化后处理
     * 📋 功能：打印Bean初始化完成信息
     * @param bean Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例
     */
    public Object afterInitializeBean(Object bean, String beanName) {
        System.out.println(beanName + "初始化完成");
        return bean;
    }
}

