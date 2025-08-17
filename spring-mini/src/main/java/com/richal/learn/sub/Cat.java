package com.richal.learn.sub;

import com.richal.learn.Autowired;
import com.richal.learn.Component;
import com.richal.learn.PostConstruct;

/**
 * 🐱 Cat - 复杂Bean示例
 * 
 * 📋 作用：展示Spring Bean特性
 * 🔍 流程：
 * 1. @Component标记为Spring组件
 * 2. @Autowired依赖注入
 * 3. @PostConstruct初始化回调
 * 
 * 💡 Spring Bean特性演示：
 * - 组件扫描：通过@Component自动被发现和注册
 * - 依赖注入：通过@Autowired自动注入Dog依赖
 * - 生命周期：通过@PostConstruct执行初始化逻辑
 * - 循环依赖：演示Spring如何处理Bean间的相互依赖
 * 
 * 🔄 Spring容器中的Bean生命周期：
 * - 扫描发现：包扫描时发现@Component标记的Cat类
 * - 定义注册：创建BeanDefinition并注册到容器
 * - 实例创建：通过无参构造器创建Cat实例
 * - 依赖注入：自动注入Dog类型的依赖
 * - 初始化：执行@PostConstruct标记的init方法
 * - 后处理：执行BeanPostProcessor的after方法
 * - 完成注册：Bean完全初始化，存储到IOC容器
 */
@Component  // 🏷️ 使用默认Bean名称"Cat"
public class Cat {

    /**
     * 🐕 依赖注入字段 - Dog类型依赖
     * 📋 作用：通过@Autowired自动注入Dog实例
     * 🔍 状态：当前版本注入逻辑未实现，字段为null
     */
    @Autowired
    private Dog dog;

    /**
     * 🚀 Bean初始化方法 - 生命周期回调
     * 📋 作用：在Bean初始化后执行自定义逻辑
     * 🔍 时机：构造器 -> 依赖注入 -> 初始化回调
     */
    @PostConstruct
    public void init() {
        System.out.println("Cat init");
        
        // 💭 可在此添加更多初始化逻辑...
    }
    
    // 🔧 可以添加更多业务方法来演示Bean的功能：
    
    /**
     * 🎯 业务方法示例 - 演示Bean使用
     * @return Cat与Dog互动描述
     */
    public String playWithDog() {
        if (dog != null) {
            return "Cat is playing with " + dog.getClass().getSimpleName();
        } else {
            return "Cat is alone (Dog dependency not injected yet)";
        }
    }
    
    /**
     * 🔍 获取依赖的Dog实例
     * @return 注入的Dog实例，未注入返回null
     */
    public Dog getDog() {
        return dog;
    }

}
