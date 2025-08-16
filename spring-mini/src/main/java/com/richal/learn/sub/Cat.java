package com.richal.learn.sub;

import com.richal.learn.Autowired;
import com.richal.learn.Component;
import com.richal.learn.PostConstruct;

/**
 * 🐱 Cat - 复杂Bean示例类
 * 
 * 📚 示例说明：
 * 这是一个展示Spring完整特性的Bean示例，包含：
 * 1. @Component注解：标记为Spring管理的组件
 * 2. @Autowired注解：演示依赖注入（虽然当前版本未实现注入逻辑）
 * 3. @PostConstruct注解：演示Bean生命周期管理
 * 
 * 🏷️ Bean配置：
 * - 注解：@Component（使用默认名称"Cat"）
 * - Bean名称：Cat（类名首字母小写，遵循默认命名规则）
 * - 依赖：Dog类型的Bean（通过@Autowired注入）
 * - 初始化：包含@PostConstruct初始化方法
 * 
 * 🔄 Bean生命周期演示：
 * 1. 📦 实例化：Spring调用Cat的无参构造器创建实例
 * 2. 💉 依赖注入：Spring为@Autowired标记的dog字段注入Dog实例（当前版本待实现）
 * 3. 🚀 初始化回调：Spring调用@PostConstruct标记的init()方法
 * 4. ✅ Bean就绪：Cat实例完全初始化，可以被其他组件使用
 * 
 * 🎯 在容器中的使用：
 * ```java
 * ApplicationContext context = new ApplicationContext("com.richal.learn");
 * 
 * // 通过默认名称获取
 * Cat cat1 = (Cat) context.getBean("Cat");
 * 
 * // 通过类型获取
 * Cat cat2 = context.getBean(Cat.class);
 * 
 * // 验证单例模式
 * System.out.println(cat1 == cat2); // true
 * 
 * // 此时cat.dog字段应该已经注入了Dog实例（待实现功能）
 * // System.out.println(cat1.dog != null); // 应该为true
 * ```
 * 
 * 🚨 当前版本限制：
 * - @Autowired依赖注入功能还未实现，dog字段会保持null
 * - 实际使用时需要完善依赖注入逻辑
 * 
 * 💡 设计目的：
 * - 演示Spring Bean的完整特性使用
 * - 展示依赖注入的基本用法（框架层面）
 * - 验证Bean生命周期管理功能
 * - 作为其他Bean的依赖对象
 */
@Component  // 🏷️ 使用默认Bean名称"Cat"
public class Cat {

    /**
     * 🐕 依赖注入字段 - Dog类型的依赖对象
     * 
     * 📋 注入说明：
     * - @Autowired：标记该字段需要Spring自动注入
     * - private：体现封装性，依赖通过容器注入而不是直接访问
     * - Dog类型：Spring会在容器中查找Dog类型的Bean进行注入
     * 
     * 🔍 注入策略（设计中，当前未实现）：
     * 1. 按类型查找：在IOC容器中查找Dog类型的Bean
     * 2. 唯一性检查：确保只有一个匹配的Bean（避免歧义）
     * 3. 反射注入：使用Field.set()方法将Bean实例注入到字段中
     * 
     * 🚨 当前状态：
     * - 注解已正确标记，但注入逻辑未实现
     * - 运行时该字段会保持null值
     * - 需要在ApplicationContext中添加依赖注入处理逻辑
     */
    @Autowired
    private Dog dog;

    /**
     * 🚀 Bean初始化方法 - PostConstruct生命周期回调
     * 
     * 📋 功能说明：
     * 这是一个生命周期回调方法，在Bean完全初始化后自动执行
     * 
     * 🔄 执行时机：
     * 1. 构造器执行完成 → Bean实例创建
     * 2. 依赖注入完成 → 所有@Autowired字段都已设置（理想情况）
     * 3. @PostConstruct方法执行 → 自定义初始化逻辑 ← 当前阶段
     * 4. Bean就绪可用 → 可以被其他组件使用
     * 
     * 🎯 使用场景：
     * - 初始化资源：建立数据库连接、加载配置文件等
     * - 验证状态：检查依赖注入是否正确完成
     * - 启动服务：启动后台线程、注册监听器等
     * - 预处理数据：加载缓存、预计算数据等
     * 
     * 💡 方法约束：
     * - 必须是无参数方法
     * - 可以是任意访问修饰符（public、private等）
     * - 不能是static方法
     * - 可以抛出异常（异常会导致Bean创建失败）
     * 
     * 🔍 实际输出：
     * 当容器启动时，会在控制台看到"Cat init"输出，
     * 表明该Bean的初始化回调已成功执行
     */
    @PostConstruct
    public void init() {
        System.out.println("Cat init");
        
        // 💭 在实际应用中，这里可以添加更多初始化逻辑：
        // - 验证依赖注入是否成功：
        //   if (dog == null) { throw new IllegalStateException("Dog dependency not injected"); }
        // - 初始化业务数据：
        //   loadConfiguration();
        // - 启动后台服务：
        //   startBackgroundTasks();
    }
    
    // 🔧 可以添加更多业务方法来演示Bean的功能：
    
    /**
     * 🎯 业务方法示例 - 演示Bean的实际使用
     * 
     * @return Cat与Dog的互动描述
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
     * 
     * @return 注入的Dog实例，如果未注入则返回null
     */
    public Dog getDog() {
        return dog;
    }

}
