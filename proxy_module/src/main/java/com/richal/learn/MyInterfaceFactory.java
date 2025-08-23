package com.richal.learn;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态代理的工厂类
 * 
 * 这是整个动态代理系统的核心工厂，负责：
 * 1. 动态生成代理类的Java源代码
 * 2. 编译生成的源代码
 * 3. 创建代理对象实例
 * 4. 管理代理类的生命周期
 * 
 * 核心流程：
 * 1. 根据处理器生成唯一的类名
 * 2. 生成包含方法实现的Java源代码
 * 3. 将源代码写入文件
 * 4. 编译源代码生成字节码
 * 5. 使用反射创建代理对象实例
 * 6. 调用处理器的setProxy方法进行初始化
 * 
 * 技术特点：
 * - 使用原子计数器确保类名唯一性
 * - 支持运行时代码生成和编译
 * - 使用URLClassLoader动态加载生成的类
 * - 自动管理文件路径和包结构
 * 
 * 设计模式：工厂模式 + 建造者模式 + 策略模式
 * 应用场景：AOP框架、ORM框架、RPC框架、插件系统等
 */
public class MyInterfaceFactory {

    /**
     * 原子计数器，用于生成唯一的类名
     * 
     * 使用AtomicInteger确保在多线程环境下的安全性，
     * 每次调用getAndIncrement()都会返回当前值并递增
     */
    private static AtomicInteger count = new AtomicInteger(0);

    /**
     * 创建代理类的Java源文件
     * 
     * 这个方法负责：
     * 1. 确保包目录存在
     * 2. 根据处理器生成方法实现代码
     * 3. 生成完整的Java类源代码
     * 4. 将源代码写入文件系统
     * 
     * 生成的代理类特点：
     * - 实现MyInterface接口
     * - 包含myInterface字段用于委托调用
     * - 方法实现由MyHandler决定
     * - 自动生成包声明和类结构
     * 
     * @param className 要生成的类名
     * @param myHandler 处理器，决定方法的具体实现
     * @return 生成的Java源文件
     * @throws IOException 文件操作异常
     * 
     * 文件结构：
     * - 包声明：package com.richal.learn;
     * - 类声明：public class {className} implements MyInterface
     * - 字段：private MyInterface myInterface;
     * - 方法：根据处理器生成的具体实现
     */
    private static File createFile(String className, MyHandler myHandler) throws IOException {
        // 确保在proxy_module目录下创建文件
        String packagePath = "src/main/java/com/richal/learn/";
        File packageDir = new File(packagePath);
        if (!packageDir.exists()) {
            packageDir.mkdirs();
        }

        // 根据处理器生成各个方法的实现代码
        String func1Body = myHandler.functionBody("method1");
        String func2Body = myHandler.functionBody("method2");
        String func3Body = myHandler.functionBody("method3");

        // 生成完整的Java类源代码
        String context = "package com.richal.learn;\n" +
                "\n" +
                "public class " + className + " implements MyInterface{\n" +
                "    private MyInterface myInterface;\n" +
                "    \n" +
                "    @Override\n" +
                "    public void method1() {\n" +
                "\n" +
                        func1Body +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void method2() {\n" +
                "\n" +
                func2Body +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void method3() {\n" +
                "\n" +
                func3Body +
                "    }\n" +
                "}\n";
        
        // 将源代码写入文件
        File file = new File(packagePath + className + ".java");
        Files.write(file.toPath(), context.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    /**
     * 生成唯一的类名
     * 
     * 使用"MyInterfaceProxy"前缀加上递增的计数器值，
     * 确保每次生成的类名都是唯一的。
     * 
     * @return 唯一的类名，如：MyInterfaceProxy0, MyInterfaceProxy1等
     */
    private static String getClassName() {
        // 修复类名，使用更合理的命名
        return "MyInterfaceProxy" + count.getAndIncrement();
    }

    /**
     * 创建代理对象实例
     * 
     * 这个方法负责：
     * 1. 创建URLClassLoader加载编译后的类
     * 2. 使用反射获取类的构造函数
     * 3. 创建代理对象实例
     * 4. 调用处理器的setProxy方法进行初始化
     * 
     * 类加载机制：
     * - 使用URLClassLoader动态加载生成的类
     * - 类路径指向target/classes目录
     * - 支持运行时加载新编译的类
     * 
     * @param className 要实例化的类名
     * @return 代理对象实例
     * @throws Exception 反射操作或类加载异常
     * 
     * 注意事项：
     * - 确保类已经成功编译
     * - 类名必须包含完整的包路径
     * - 需要正确处理ClassLoader的关闭
     */
    private static MyInterface newInstance(String className) throws Exception {
        // 使用URLClassLoader动态加载编译后的类
        File classesDir = new File("target/classes");
        URLClassLoader classLoader = new URLClassLoader(new URL[]{classesDir.toURI().toURL()});
        
        try {
            // 加载类
            Class<?> aClass = classLoader.loadClass("com.richal.learn." + className);
            Constructor<?> constructor = aClass.getConstructor();
            MyInterface proxy = (MyInterface)constructor.newInstance();
            
            return proxy;
        } finally {
            // 关闭类加载器
            classLoader.close();
        }
    }

    /**
     * 创建代理对象的公共接口
     * 
     * 这是整个动态代理系统的主要入口方法，完整的流程包括：
     * 1. 生成唯一的类名
     * 2. 创建Java源文件
     * 3. 编译源代码
     * 4. 创建代理对象实例
     * 5. 调用处理器的初始化方法
     * 
     * 使用方式：
     * MyInterface proxy = MyInterfaceFactory.createProxyObject(new LogHandler(target));
     * 
     * @param myHandler 处理器，决定代理对象的行为
     * @return 配置好的代理对象
     * @throws Exception 创建过程中的各种异常
     * 
     * 设计优势：
     * - 支持不同的处理器策略
     * - 自动管理类的生成和编译
     * - 提供统一的创建接口
     * - 支持运行时动态创建
     */
    public static MyInterface createProxyObject(MyHandler myHandler) throws Exception {
        // 生成唯一的类名
        String className = getClassName();
        
        // 创建Java源文件
        File file = createFile(className, myHandler);
        
        // 编译源代码
        Compiler.compile(file);
        
        // 创建代理对象实例
        MyInterface proxy = newInstance(className);
        
        // 调用处理器的setProxy方法进行初始化
        myHandler.setProxy(proxy);
        
        return proxy;
    }
}
