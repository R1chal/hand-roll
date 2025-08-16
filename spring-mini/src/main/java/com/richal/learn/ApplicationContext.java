package com.richal.learn;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🏢 ApplicationContext - Spring IOC容器的核心实现
 * 
 * 📚 核心职责：
 * 这是整个MiniSpring框架的心脏，负责管理Bean的完整生命周期：
 * 1. 🔍 发现：扫描指定包下的@Component注解类
 * 2. 📋 注册：将类的元数据封装为BeanDefinition并注册
 * 3. 🏭 创建：根据BeanDefinition实例化Bean对象
 * 4. 💉 注入：为Bean的@Autowired字段注入依赖
 * 5. 🚀 初始化：执行Bean的@PostConstruct初始化方法
 * 6. 📦 管理：提供获取Bean的各种方式
 * 
 * 🎯 IOC(控制反转)原理：
 * - 传统方式：对象自己创建和管理依赖 → 紧耦合
 * - IOC方式：容器负责创建对象和注入依赖 → 松耦合
 * - 好处：代码更简洁、易测试、易扩展、符合开放封闭原则
 * 
 * 🔄 容器启动流程：
 * 1. 📦 包扫描(scanPackage) → 发现所有.class文件
 * 2. 🔍 组件筛选(scanCreate) → 过滤出@Component标记的类
 * 3. 📋 元数据封装(wrapper) → 创建BeanDefinition对象
 * 4. 🏭 Bean创建(createBean) → 实例化Bean并执行初始化
 * 
 * 💡 设计模式应用：
 * - 工厂模式：ApplicationContext是创建Bean的工厂
 * - 单例模式：IOC容器中的Bean默认都是单例
 * - 观察者模式：Bean生命周期的各个回调点
 * - 策略模式：不同的Bean获取策略(按名称、按类型等)
 * 
 * 🚨 当前版本限制：
 * - 不支持循环依赖处理
 * - 不支持@Autowired依赖注入（只实现了框架，未完成注入逻辑）
 * - 不支持Bean的scope配置（默认单例）
 * - 不支持懒加载(lazy-init)
 */
public class ApplicationContext {

    /**
     * 🗂️ IOC容器 - Bean实例的存储仓库
     * 
     * 📋 数据结构说明：
     * - Key: Bean的名称标识(String)
     * - Value: Bean的实例对象(Object)
     * 
     * 🔍 设计考量：
     * - 使用HashMap保证快速的Bean查找性能 O(1)
     * - Key使用String而不是Class，支持同一类型的多个Bean实例
     * - Value使用Object，支持存储任意类型的Bean
     * 
     * 💡 实际Spring的实现：
     * - 实际Spring使用更复杂的数据结构，如ConcurrentHashMap
     * - 支持分层容器、父子容器的概念
     * - 包含更多的缓存层，如三级缓存解决循环依赖
     */
    private Map<String, Object> ioc = new HashMap<>();

    /**
     * 📋 Bean定义映射表 - Bean元数据的注册中心
     * 
     * 📋 数据结构说明：
     * - Key: Bean的名称标识(String)
     * - Value: Bean的定义信息(BeanDefinition)
     * 
     * 🔍 存在的意义：
     * - 分离Bean的定义信息和实例信息
     * - 支持Bean的延迟创建和按需创建
     * - 为后续的依赖分析和循环依赖处理提供基础
     * - 缓存反射信息，提高Bean创建性能
     * 
     * 🔄 与ioc容器的关系：
     * beanDefinitionMap存储"如何创建Bean"
     * ioc存储"已创建的Bean实例"
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 🚀 构造器 - 容器启动入口
     * 
     * 📋 功能说明：
     * 接收包名参数，启动IOC容器的完整初始化流程
     * 
     * 🔄 启动流程：
     * 1. scanPackage: 扫描指定包下的所有类
     * 2. scanCreate: 过滤出标记@Component的类
     * 3. wrapper: 将类信息封装为BeanDefinition
     * 4. createBean: 创建Bean实例并完成初始化
     * 
     * @param packageName 要扫描的包名，如"com.example.service"
     * @throws IOException 如果包扫描过程中发生IO异常
     */
    public ApplicationContext(String packageName) throws IOException {
        // 🚀 启动容器初始化流程
        initContext(packageName);
    }

    /**
     * 🔄 容器初始化方法 - Spring容器启动的核心流程
     * 
     * 📋 流程设计说明：
     * 使用流式API设计，体现函数式编程思想，代码简洁易读
     * 
     * 🔍 详细流程：
     * 1. scanPackage(packageName) → 扫描包，返回List<Class<?>>
     * 2. .stream() → 转换为流，支持链式操作
     * 3. .filter(this::scanCreate) → 过滤@Component类
     * 4. .map(this::wrapper) → 转换为BeanDefinition
     * 5. .forEach(this::createBean) → 创建Bean实例
     * 
     * 💡 设计优势：
     * - 链式调用，逻辑清晰
     * - 每个方法职责单一，易于测试和维护
     * - 支持流水线式的处理方式
     * 
     * @param packageName 要扫描的基础包名
     * @throws IOException 包扫描异常
     */
    public void initContext(String packageName) throws IOException {
        scanPackage(packageName)        // 🔍 阶段1：包扫描 - 发现所有类
                .stream()
                .filter(this::scanCreate)   // 🔍 阶段2：组件筛选 - 过滤@Component类
                .map(this::wrapper)         // 📋 阶段3：定义封装 - 创建BeanDefinition
                .forEach(this::createBean); // 🏭 阶段4：Bean创建 - 实例化并初始化
    }

    /**
     * 🏭 Bean创建入口 - 带重复检查的Bean创建
     * 
     * 📋 功能说明：
     * 这是Bean创建的入口方法，包含重复性检查，避免重复创建同名Bean
     * 
     * 🔍 处理逻辑：
     * 1. 检查IOC容器中是否已存在同名Bean
     * 2. 如果存在则跳过创建（避免重复）
     * 3. 如果不存在则调用doCreateBean执行实际创建
     * 
     * 💡 设计模式：
     * - 模板方法模式：定义创建Bean的基本框架
     * - 防护模式：通过检查避免重复操作
     * 
     * @param beanDefinition Bean的定义信息，包含创建所需的元数据
     */
    protected void createBean(BeanDefinition beanDefinition) {
        String name = beanDefinition.getName();
        
        // 🔍 防重复检查：避免同一个Bean被创建多次
        if (ioc.containsKey(name)) {
            return; // Bean已存在，跳过创建
        }
        
        // 🏭 执行实际的Bean创建逻辑
        doCreateBean(beanDefinition);
    }

    /**
     * 🏭 Bean实例化核心方法 - 实际执行Bean的创建和初始化
     * 
     * 📋 功能说明：
     * 这是Bean创建的核心实现，负责完成Bean的完整生命周期：
     * 1. 实例化：通过反射调用构造器创建Bean实例
     * 2. 初始化：执行@PostConstruct标记的初始化方法
     * 3. 注册：将创建好的Bean实例放入IOC容器
     * 
     * 🔄 详细流程：
     * Constructor.newInstance() → PostConstruct方法执行 → 注册到IOC容器
     * 
     * 🚨 注意：当前实现的限制
     * - 缺少依赖注入阶段：应该在实例化后、初始化前进行@Autowired字段注入
     * - 异常处理简化：实际应该区分不同类型的异常并给出具体提示
     * 
     * 💡 完整的Bean生命周期应该是：
     * 1. 实例化 (✅已实现)
     * 2. 依赖注入 (❌待实现：处理@Autowired字段)
     * 3. 初始化回调 (✅已实现：@PostConstruct)
     * 4. 使用阶段 (✅已实现：存储到IOC容器)
     * 5. 销毁回调 (❌未实现：@PreDestroy)
     * 
     * @param beanDefinition Bean的定义信息
     * @throws RuntimeException 如果Bean创建过程中发生任何异常
     */
    private void doCreateBean(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = null;
        
        try {
            // 🏗️ 步骤1：实例化 - 通过无参构造器创建Bean实例
            bean = constructor.newInstance();
            
            // 💉 步骤2：依赖注入阶段 (当前版本缺失)
            // TODO: 在这里应该处理@Autowired字段的依赖注入
            // processAutowiredFields(bean, beanDefinition);
            
            // 🚀 步骤3：初始化回调 - 执行@PostConstruct方法
            Method postConstructMethod = beanDefinition.getPostConstructMethod();
            if (postConstructMethod != null) {
                postConstructMethod.invoke(bean); // 调用初始化方法
            }
            
        } catch (Exception e) {
            // 🚨 异常处理：Bean创建失败时，包装异常并重新抛出
            throw new RuntimeException("创建Bean失败: " + beanDefinition.getName(), e);
        }
        
        // 📦 步骤4：注册到容器 - 将创建好的Bean存储到IOC容器中
        ioc.put(beanDefinition.getName(), bean);
    }

    /**
     * 📋 Bean定义包装器 - 将Class转换为BeanDefinition
     * 
     * 📋 功能说明：
     * 1. 将扫描到的Class对象封装为BeanDefinition
     * 2. 进行Bean名称的唯一性检查
     * 3. 将BeanDefinition注册到定义映射表中
     * 
     * 🔍 处理逻辑：
     * - 创建BeanDefinition实例（自动解析@Component、构造器、@PostConstruct）
     * - 检查Bean名称是否重复，避免覆盖已有Bean
     * - 注册到beanDefinitionMap中，为后续Bean创建做准备
     * 
     * 🚨 异常情况：
     * - 如果发现同名Bean，直接抛出异常终止启动
     * - 这体现了"快速失败"原则，避免运行时的不确定性
     * 
     * @param type 扫描到的@Component标记的类
     * @return 封装后的BeanDefinition对象
     * @throws RuntimeException 如果发现Bean名称重复
     */
    protected BeanDefinition wrapper(Class<?> type) {
        // 🏗️ 创建BeanDefinition，自动解析类的元数据
        BeanDefinition beanDefinition = new BeanDefinition(type);
        
        // 🔍 唯一性检查：确保Bean名称不重复
        if (beanDefinitionMap.containsKey(beanDefinition.getName())) {
            throw new RuntimeException("Bean名称重复: " + beanDefinition.getName() 
                + "，请检查@Component注解的name属性或类名是否冲突");
        }
        
        // 📋 注册Bean定义：存储到定义映射表中
        beanDefinitionMap.put(beanDefinition.getName(), beanDefinition);
        
        return beanDefinition;
    }

    /**
     * 🔍 组件扫描过滤器 - 判断类是否为Spring管理的组件
     * 
     * 📋 功能说明：
     * 检查给定的类是否标记了@Component注解，确定是否需要被Spring容器管理
     * 
     * 🎯 过滤规则：
     * - 有@Component注解 → true（需要被容器管理）
     * - 无@Component注解 → false（忽略该类）
     * 
     * 💡 扩展说明：
     * 实际Spring框架还支持：
     * - @Service、@Repository、@Controller等衍生注解
     * - @Configuration配置类
     * - @Bean方法定义的Bean
     * - XML配置文件定义的Bean
     * 
     * @param clazz 待检查的类对象
     * @return true表示该类是Spring组件，false表示忽略
     */
    protected boolean scanCreate(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class);
    }

    /**
     * 📦 包扫描器 - 递归扫描指定包下的所有Class文件
     * 
     * 📋 功能说明：
     * 扫描指定包名下的所有.class文件，并通过反射加载为Class对象
     * 
     * 🔍 实现原理：
     * 1. 通过ClassLoader获取包对应的文件系统路径
     * 2. 使用Java NIO的Files.walkFileTree递归遍历目录
     * 3. 过滤出.class文件，转换为完整的类名
     * 4. 通过Class.forName加载类对象
     * 
     * 🎯 路径转换逻辑：
     * 文件路径 → 类名的转换过程：
     * /path/to/com/example/UserService.class
     * → com.example.UserService.class
     * → com.example.UserService
     * 
     * 💡 技术细节：
     * - 使用SimpleFileVisitor访问者模式遍历文件树
     * - 支持嵌套包的递归扫描
     * - 自动处理包名和文件路径的转换
     * 
     * 🚨 潜在问题：
     * - 当前实现假设类路径结构标准，可能在某些特殊环境下失效
     * - 没有处理JAR包内的类扫描（实际Spring支持）
     * 
     * @param packageName 要扫描的包名，如"com.example.service"
     * @return 扫描到的所有Class对象列表
     * @throws IOException 如果文件系统访问失败
     */
    private List<Class<?>> scanPackage(String packageName) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        
        // 🔍 步骤1：获取包对应的文件系统路径
        // 将包名的点号转换为文件分隔符：com.example → com/example
        URL resource = this.getClass().getClassLoader()
            .getResource(packageName.replace(".", File.separator));
        
        Path path = Path.of(resource.getFile());
        
        // 🔍 步骤2：递归遍历目录树，查找所有.class文件
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            /**
             * 🔍 文件访问器 - 处理遍历到的每个文件
             * 
             * 📋 处理逻辑：
             * 1. 检查文件是否为.class文件
             * 2. 将文件路径转换为完整的类名
             * 3. 通过反射加载类对象
             * 4. 添加到结果列表中
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path absolutePath = file.toAbsolutePath();
                
                // 🔍 只处理.class文件，忽略其他文件
                if (absolutePath.toString().endsWith(".class")) {
                    // 🔄 路径转换：文件路径 → 类名
                    String replaced = absolutePath.toString().replace(File.separator, ".");
                    
                    // 🔍 提取完整类名：从包名开始截取，去掉.class后缀
                    int packageIndex = replaced.indexOf(packageName);
                    String className = replaced.substring(packageIndex, 
                        replaced.length() - ".class".length());
                    
                    try {
                        // 🔍 反射加载类对象
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        // 🚨 类加载失败，包装异常重新抛出
                        throw new RuntimeException("无法加载类: " + className, e);
                    }
                }
                
                // 继续访问其他文件
                return FileVisitResult.CONTINUE;
            }
        });
        
        return classes;
    }

    /**
     * 🔍 根据名称获取Bean - 最基础的Bean查找方式
     * 
     * 📋 功能说明：
     * 通过Bean的名称从IOC容器中获取对应的Bean实例
     * 
     * 🎯 使用场景：
     * - 当明确知道Bean名称时使用
     * - 避免类型转换，直接返回Object类型
     * 
     * 💡 名称规则：
     * - 默认名称：类名首字母小写（UserService → userService）
     * - 自定义名称：@Component(name="custom")指定的名称
     * 
     * @param beanName Bean的名称标识
     * @return Bean实例，如果不存在返回null
     */
    public Object getBean(String beanName) {
        return ioc.get(beanName);
    }

    /**
     * 🔍 根据类型获取Bean - 类型安全的Bean查找方式
     * 
     * 📋 功能说明：
     * 通过Bean的类型从IOC容器中查找匹配的Bean实例，支持继承和实现关系
     * 
     * 🔍 匹配规则：
     * - 精确匹配：Bean的类型与指定类型完全一致
     * - 继承匹配：Bean是指定类型的子类
     * - 实现匹配：Bean实现了指定的接口
     * 
     * 🎯 使用场景：
     * - 类型安全：返回指定类型，无需强制转换
     * - 接口编程：通过接口类型获取实现类实例
     * 
     * 🚨 注意事项：
     * - 如果有多个匹配的Bean，只返回第一个找到的
     * - 如果没有匹配的Bean，返回null（实际Spring会抛出异常）
     * 
     * @param <T> Bean的类型参数
     * @param clazz Bean的类型Class对象
     * @return 匹配的Bean实例，类型安全，如果不存在返回null
     */
    public <T> T getBean(Class<T> clazz) {
        return this.ioc.values()
                .stream()
                .filter(bean -> clazz.isAssignableFrom(bean.getClass())) // 类型匹配检查
                .map(bean -> (T) bean)                                   // 类型转换
                .findAny()                                               // 找到任意一个匹配的
                .orElse(null);                                          // 没找到返回null
    }

    /**
     * 🔍 根据类型获取所有Bean - 批量Bean查找方式
     * 
     * 📋 功能说明：
     * 获取容器中所有匹配指定类型的Bean实例，返回列表
     * 
     * 🎯 使用场景：
     * - 获取某个接口的所有实现类
     * - 批量处理同类型的Bean
     * - 插件式架构：动态获取所有插件实例
     * 
     * 💡 实际应用示例：
     * ```java
     * // 获取所有MessageHandler接口的实现
     * List<MessageHandler> handlers = context.getBeans(MessageHandler.class);
     * // 批量处理消息
     * handlers.forEach(handler -> handler.handle(message));
     * ```
     * 
     * 🔍 匹配规则：与getBean(Class<T>)相同，支持继承和实现关系
     * 
     * @param <T> Bean的类型参数
     * @param beanType Bean的类型Class对象
     * @return 所有匹配的Bean实例列表，如果没有匹配的返回空列表
     */
    public <T> List<T> getBeans(Class<T> beanType) {
        return this.ioc.values()
                .stream()
                .filter(bean -> beanType.isAssignableFrom(bean.getClass())) // 类型匹配检查
                .map(bean -> (T) bean)                                      // 类型转换
                .toList();                                                  // 收集为列表
    }

}