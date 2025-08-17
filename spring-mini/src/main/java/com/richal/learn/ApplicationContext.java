package com.richal.learn;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
 * 🏢 ApplicationContext - Spring IOC容器核心
 * 
 * 📋 职责：管理Bean生命周期
 * 1. 扫描@Component类
 * 2. 注册Bean定义
 * 3. 创建Bean实例
 * 4. 注入依赖
 * 5. 初始化Bean
 * 6. 提供Bean获取方法
 * 
 * 🔄 流程：扫描 -> 注册 -> 创建 -> 注入 -> 初始化
 * 
 * 💡 Spring框架核心设计思想：
 * - 控制反转(IoC)：将对象的创建和依赖管理交给容器
 * - 依赖注入(DI)：通过反射自动注入对象间的依赖关系
 * - 生命周期管理：提供完整的Bean生命周期钩子
 * - 单例管理：确保每个Bean在容器中只有一个实例
 * - 延迟加载：支持按需创建Bean，提高启动性能
 * 
 * 🔄 Spring容器启动的完整流程：
 * - 阶段1：包扫描 - 发现所有@Component标记的类
 * - 阶段2：Bean定义注册 - 将类信息封装为BeanDefinition
 * - 阶段3：后处理器初始化 - 优先创建BeanPostProcessor
 * - 阶段4：Bean实例化 - 创建所有Bean并完成依赖注入
 * - 阶段5：Bean初始化 - 执行@PostConstruct和后处理器
 * - 阶段6：容器就绪 - 所有Bean可用，容器启动完成
 */
public class ApplicationContext {

    /**
     * 🗂️ IOC容器 - 存储Bean实例
     * 📋 结构：Key为Bean名称，Value为Bean对象
     * 🔍 特点：快速查找，使用HashMap
     * 💡 设计：单例模式，确保每个Bean只有一个实例
     */
    private Map<String, Object> ioc = new HashMap<>();

    /**
     * 📋 Bean定义映射表 - 存储Bean元数据
     * 📋 结构：Key为Bean名称，Value为BeanDefinition
     * 🔍 作用：缓存反射信息，支持延迟创建
     * 💡 设计：元数据驱动，先注册定义，按需创建实例
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 🔄 正在创建的Bean缓存 - 防止循环依赖
     * 📋 结构：Key为Bean名称，Value为正在创建的Bean实例
     * 🔍 作用：解决循环依赖问题，避免无限递归
     * 💡 设计：早期引用，在Bean完全初始化前就提供引用
     */
    private Map<String, Object> loadingIoc = new HashMap<>();

    /**
     * 🛠️ Bean后处理器列表 - 扩展Bean生命周期
     * 📋 结构：存储所有BeanPostProcesser实现
     * 🔍 作用：在Bean初始化前后执行自定义逻辑
     * 💡 设计：责任链模式，支持多个后处理器串联执行
     */
    private List<BeanPostProcesser> beanPostProcessors = new ArrayList<>();

    /**
     * 🚀 构造器 - 容器启动入口
     * 📋 功能：接收包名，启动IOC初始化流程
     * 🔄 流程：扫描包 -> 过滤组件 -> 注册定义 -> 创建Bean
     * @param packageName 要扫描的包名
     * @throws IOException 包扫描异常
     * 
     * 💡 Spring容器启动设计：
     * - 构造即启动：容器创建时自动完成初始化
     * - 包扫描策略：支持指定包路径，灵活控制扫描范围
     * - 异常处理：启动失败时抛出异常，确保容器状态一致
     */
    public ApplicationContext(String packageName) throws IOException {
        // 🚀 启动容器初始化流程
        initContext(packageName);
    }

    /**
     * 🔄 容器初始化方法 - 启动核心流程
     * 🔍 流程：
     * 1. 扫描包获取类
     * 2. 过滤@Component类
     * 3. 封装为BeanDefinition
     * 4. 创建Bean实例
     * @param packageName 要扫描的包名
     * @throws IOException 包扫描异常
     * 
     * 💡 Spring容器初始化策略：
     * - 分阶段执行：扫描 -> 注册 -> 创建，逻辑清晰
     * - 流式处理：使用Stream API，代码简洁高效
     * - 后处理器优先：先初始化BeanPostProcessor，再处理其他Bean
     * - 批量创建：统一创建所有Bean，确保依赖关系正确
     */
    public void initContext(String packageName) throws IOException {
        scanPackage(packageName)        // 🔍 阶段1：包扫描 - 发现所有类
                .stream()
                .filter(this::scanCreate)   // 🔍 阶段2：组件筛选 - 过滤@Component类
                .forEach(this::wrapper);    // 📋 阶段3：Bean定义注册 - 封装为BeanDefinition
        initBeanPostprocessor();            // 🛠️ 阶段4：后处理器初始化 - 优先初始化BeanPostProcessor
        beanDefinitionMap.values().forEach(this::createBean); // 🏭 阶段5：Bean创建 - 实例化并初始化
    }

    /**
     * 🛠️ 初始化Bean后处理器 - 优先创建后处理器
     * 📋 功能：识别并创建所有BeanPostProcessor实现
     * 🔍 流程：
     * 1. 筛选BeanPostProcessor类型的Bean
     * 2. 创建后处理器实例
     * 3. 添加到后处理器列表
     * 
     * 💡 设计要点：
     * - 优先级处理：后处理器必须先于其他Bean创建
     * - 类型识别：使用isAssignableFrom判断类型兼容性
     * - 循环依赖：后处理器也可能有依赖，需要递归创建
     */
    private void initBeanPostprocessor() {
        beanDefinitionMap.values()
                .stream()
                .filter(bd -> BeanPostProcesser.class.isAssignableFrom(bd.getBeantype()))
                .map(this::createBean)
                .map(bean -> (BeanPostProcesser) bean)
                .forEach(beanPostProcessors::add);
    }

    /**
     * 🏭 Bean创建入口 - 避免重复创建
     * 📋 功能：检查是否已存在Bean，若无则创建
     * 🔍 流程：检查IOC -> 若存在则返回 -> 否则执行创建
     * @param beanDefinition Bean定义信息
     * @return 创建或已存在的Bean实例
     * 
     * 💡 单例模式设计：
     * - 重复检查：避免同一个Bean被创建多次
     * - 循环依赖处理：通过loadingIoc缓存正在创建的Bean
     * - 线程安全：当前实现非线程安全，生产环境需要加锁
     */
    protected Object createBean(BeanDefinition beanDefinition) {
        String name = beanDefinition.getName();

        // 🔍 防重复检查：避免同一个Bean被创建多次
        if (ioc.containsKey(name)) {
            return ioc.get(name); // Bean已存在，跳过创建
        }

        // 🔄 循环依赖检查：如果Bean正在创建中，返回早期引用
        if (loadingIoc.containsKey(name)) {
            return loadingIoc.get(name); // 返回正在创建的Bean引用
        }

        // 🏭 执行实际的Bean创建逻辑
        return doCreateBean(beanDefinition);
    }

    /**
     * 🏭 Bean实例化核心方法 - 创建和初始化Bean
     * 📋 功能：完成Bean生命周期
     * 🔄 流程：
     * 1. 通过构造器实例化
     * 2. 注入依赖
     * 3. 执行初始化
     * 4. 注册到IOC容器
     * @param beanDefinition Bean定义信息
     * @return 创建好的Bean实例
     * @throws RuntimeException 创建失败抛出异常
     * 
     * 💡 Spring Bean生命周期管理：
     * - 实例化：通过反射创建对象实例
     * - 早期引用：解决循环依赖问题
     * - 依赖注入：自动注入@Autowired标记的字段
     * - 初始化：执行@PostConstruct方法和后处理器
     * - 注册：将完整Bean存储到容器中
     */
    private Object doCreateBean(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = null;

        try {
            // 🏗️ 步骤1：实例化 - 通过无参构造器创建Bean实例
            bean = constructor.newInstance();

            // 🔄 步骤1.5：早期引用 - 解决循环依赖问题
            loadingIoc.put(beanDefinition.getName(), bean);
            
            // 💉 步骤2：依赖注入 - 注入@Autowired标记的字段
            processAutowiredFields(bean, beanDefinition);

            // 🚀 步骤3：Bean初始化 - 执行@PostConstruct和后处理器
            bean = initBean(beanDefinition, bean);

            // 📦 步骤4：完成注册 - 移除早期引用，正式注册到容器
            loadingIoc.remove(beanDefinition.getName());
            ioc.put(beanDefinition.getName(), bean);
        } catch (Exception e) {
            // 🚨 异常处理：Bean创建失败时，包装异常并重新抛出
            throw new RuntimeException("创建Bean失败: " + beanDefinition.getName(), e);
        }

        return bean;
    }

    private Object initBean(BeanDefinition beanDefinition, Object bean) throws IllegalAccessException, InvocationTargetException {
        beanPostProcessors.forEach(beanPostProcessor -> {
            beanPostProcessor.beforeInitializeBean(bean, beanDefinition.getName());
        });
        // 🚀 步骤3：初始化回调 - 执行@PostConstruct方法
        Method postConstructMethod = beanDefinition.getPostConstructMethod();
        if (postConstructMethod != null) {
            postConstructMethod.invoke(bean); // 调用初始化方法
        }
        beanPostProcessors.forEach(beanPostProcessor -> {
            beanPostProcessor.afterInitializeBean(bean, beanDefinition.getName());
        });
        return bean;
    }

    private void processAutowiredFields(Object bean, BeanDefinition beanDefinition) {
        beanDefinition.getAutowiredFields().forEach(field -> {
            field.setAccessible(true);
            try {
                field.set(bean, getBean(field.getType()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 📋 Bean定义包装器 - Class转BeanDefinition
     * 📋 功能：封装Class为BeanDefinition并注册
     * 🔍 流程：创建定义 -> 检查重复 -> 注册到映射表
     * @param type 扫描到的@Component类
     * @return BeanDefinition对象
     * @throws RuntimeException Bean名称重复抛出异常
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
     * 🔍 组件扫描过滤器 - 判断是否为Spring组件
     * 📋 功能：检查类是否有@Component注解
     * 🎯 规则：有注解返回true，无注解返回false
     * @param clazz 待检查的类
     * @return 是否为Spring组件
     */
    protected boolean scanCreate(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class);
    }

    /**
     * 📦 包扫描器 - 扫描指定包下的Class文件
     * 📋 功能：扫描.class文件并加载为Class对象
     * 🔍 流程：
     * 1. 获取包路径
     * 2. 遍历目录树找.class文件
     * 3. 转换为类名并加载
     * @param packageName 要扫描的包名
     * @return 扫描到的Class对象列表
     * @throws IOException 文件系统访问失败
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
             * 🔍 文件访问器 - 处理每个文件
             * 📋 流程：
             * 1. 检查是否为.class文件
             * 2. 转换路径为类名
             * 3. 加载类对象
             * 4. 添加到结果列表
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
     * 🔍 根据名称获取Bean - 基础查找方式
     * 📋 功能：通过Bean名称获取实例
     * 🎯 场景：明确知道Bean名称时使用
     * @param beanName Bean名称
     * @return Bean实例，不存在返回null
     */
    public Object getBean(String beanName) {
        if (beanName == null) {
            return null;
        }
        Object bean = ioc.get(beanName);
        if (bean != null) {
            return bean;
        }
        if (beanDefinitionMap.containsKey(beanName)) {
            return createBean(beanDefinitionMap.get(beanName));
        }
        return null;
    }

    /**
     * 🔍 根据类型获取Bean - 类型安全查找
     * 📋 功能：通过类型查找Bean实例
     * 🔍 规则：精确匹配、继承匹配、实现匹配
     * 🎯 场景：接口编程，类型安全获取
     * @param <T> Bean类型参数
     * @param clazz Bean类型Class对象
     * @return 匹配的Bean实例，不存在返回null
     */
    public <T> T getBean(Class<T> clazz) {
        String beanName = this.beanDefinitionMap.values()
                .stream()
                .filter(bd -> clazz.isAssignableFrom(bd.getBeantype()))
                .map(BeanDefinition::getName)
                .findFirst()
                .orElse(null);
        return (T) getBean(beanName);
    }

    /**
     * 🔍 根据类型获取所有Bean - 批量查找
     * 📋 功能：获取所有匹配类型的Bean实例
     * 🎯 场景：获取接口所有实现，批量处理
     * @param <T> Bean类型参数
     * @param beanType Bean类型Class对象
     * @return 匹配的Bean实例列表
     */
    public <T> List<T> getBeans(Class<T> beanType) {
        return this.beanDefinitionMap.values()
                .stream()
                .filter(bd -> beanType.isAssignableFrom(bd.getBeantype()))
                .map(BeanDefinition::getName)
                .map(this::getBean)                                    // 收集为列表
                .map((bean) -> (T) bean)
                .toList();
    }

}