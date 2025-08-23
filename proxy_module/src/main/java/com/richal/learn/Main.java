package com.richal.learn;

import java.lang.reflect.Field;

/**
 * 动态代理系统的演示主类
 * 
 * 这个类展示了整个动态代理系统的完整使用流程，包括：
 * 1. 创建不同类型的处理器
 * 2. 使用工厂生成代理对象
 * 3. 调用代理对象的方法
 * 4. 演示不同的代理行为
 * 
 * 系统架构：
 * - MyInterface: 目标接口
 * - MyHandler: 处理器接口
 * - MyInterfaceFactory: 代理对象工厂
 * - Compiler: 代码编译器
 * - 各种处理器实现类
 * 
 * 学习要点：
 * - 动态代理的实现原理
 * - AOP编程的思想
 * - 运行时代码生成和编译
 * - 反射和类加载机制
 */
public class Main {
    
    /**
     * 主方法，演示动态代理系统的完整流程
     * 
     * 执行流程：
     * 1. 创建PrintFunctionName处理器，生成简单打印的代理对象
     * 2. 创建PrintFunctionName1处理器，生成带前缀打印的代理对象
     * 3. 创建LogHandler处理器，生成带日志记录的代理对象
     * 
     * 每个步骤都会：
     * - 调用MyInterfaceFactory.createProxyObject()创建代理对象
     * - 调用代理对象的方法验证功能
     * - 输出分隔线区分不同处理器的效果
     * 
     * @param args 命令行参数（未使用）
     * @throws Exception 可能抛出的各种异常
     */
    public static void main(String[] args) throws Exception {
        // 第一步：创建简单打印处理器
        System.out.println("=== 使用PrintFunctionName处理器 ===");
        MyInterface proxyObject = MyInterfaceFactory.createProxyObject(new PrintFunctionName());
        proxyObject.method1();
        proxyObject.method2();
        proxyObject.method3();
        
        System.out.println("\n---");
        
        // 第二步：创建带前缀打印处理器
        System.out.println("=== 使用PrintFunctionName1处理器 ===");
        proxyObject = MyInterfaceFactory.createProxyObject(new PrintFunctionName1());
        proxyObject.method1();
        proxyObject.method2();
        proxyObject.method3();
        
        System.out.println("\n---");
        
        // 第三步：创建日志处理器
        System.out.println("=== 使用LogHandler处理器 ===");
        // 创建一个目标对象作为委托目标
        MyInterface targetObject = new NameAndLengthImpl();
        proxyObject = MyInterfaceFactory.createProxyObject(new LogHandler(targetObject));
        proxyObject.method1();
        proxyObject.method2();
        proxyObject.method3();
    }
    
    /**
     * 简单打印处理器
     * 
     * 这个处理器实现最简单的代理行为：
     * - 直接打印方法名
     * - 不进行任何额外的处理
     * - 适合用于演示和测试
     * 
     * 生成的代码示例：
     * System.out.println("method1");
     * 
     * 设计模式：策略模式
     * 应用场景：调试、测试、简单日志等
     */
    static class PrintFunctionName implements MyHandler {
        
        /**
         * 生成简单打印的代码
         * 
         * @param methodName 方法名
         * @return 包含打印语句的Java代码字符串
         */
        @Override
        public String functionBody(String methodName) {
            return " System.out.println(\"" + methodName + "\");";
        }
        
        /**
         * 简单处理器不需要特殊初始化
         * 
         * @param proxy 代理对象（未使用）
         */
        @Override
        public void setProxy(MyInterface proxy) {
            // 简单处理器不需要特殊处理
        }
    }
    
    /**
     * 带前缀打印处理器
     * 
     * 这个处理器在打印方法名前先打印一个标识符：
     * - 先打印数字"1"
     * - 再打印方法名
     * - 用于区分不同的代理对象
     * 
     * 生成的代码示例：
     * System.out.println(1);
     * System.out.println("method1");
     * 
     * 设计模式：装饰器模式
     * 应用场景：对象标识、调试标记、日志分类等
     */
    static class PrintFunctionName1 implements MyHandler {
        
        /**
         * 生成带前缀打印的代码
         * 
         * @param methodName 方法名
         * @return 包含前缀打印和方法名打印的Java代码字符串
         */
        @Override
        public String functionBody(String methodName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" System.out.println(1);");
            stringBuilder.append(" System.out.println(\"" + methodName + "\");");
            return stringBuilder.toString();
        }
        
        /**
         * 简单处理器不需要特殊初始化
         * 
         * @param proxy 代理对象（未使用）
         */
        @Override
        public void setProxy(MyInterface proxy) {
            // 简单处理器不需要特殊处理
        }
    }
    
    /**
     * 日志记录处理器
     * 
     * 这个处理器实现了真正的AOP功能：
     * - 在方法执行前打印"before"
     * - 调用目标对象的实际方法
     * - 在方法执行后打印"after"
     * 
     * 核心特性：
     * - 持有目标对象的引用
     * - 使用反射注入目标对象到代理对象
     * - 实现方法调用的委托
     * 
     * 生成的代码示例：
     * System.out.println("before");
     * this.myInterface.method1();
     * System.out.println("after");
     * 
     * 设计模式：代理模式 + 装饰器模式 + AOP
     * 应用场景：日志记录、性能监控、事务管理、权限控制等
     */
    static class LogHandler implements MyHandler {
        
        /**
         * 目标对象引用，用于委托调用
         * 
         * 这个字段存储了真正要执行方法的对象，
         * 代理对象会将方法调用委托给这个对象
         */
        private MyInterface myInterface;
        
        /**
         * 构造函数，接收目标对象
         * 
         * @param myInterface 要被代理的目标对象
         */
        public LogHandler(MyInterface myInterface) {
            this.myInterface = myInterface;
        }
        
        /**
         * 生成带日志记录的方法实现代码
         * 
         * 生成的代码结构：
         * 1. 前置日志：打印"before"
         * 2. 方法调用：委托给目标对象
         * 3. 后置日志：打印"after"
         * 
         * @param methodName 方法名
         * @return 包含完整日志逻辑的Java代码字符串
         */
        @Override
        public String functionBody(String methodName) {
            String context = " System.out.println(\"before\");\n" +
                             " this.myInterface." + methodName + "();\n" +
                             " System.out.println(\"after\");";
            return context;
        }
        
        /**
         * 设置代理对象，注入目标对象引用
         * 
         * 这个方法使用反射机制：
         * 1. 获取代理类的myInterface字段
         * 2. 设置字段可访问
         * 3. 将目标对象注入到代理对象中
         * 
         * 这样生成的代理对象就能通过this.myInterface调用目标对象的方法
         * 
         * @param proxy 生成的代理对象
         */
        @Override
        public void setProxy(MyInterface proxy) {
            try {
                // 使用反射获取代理类中的myInterface字段
                Field field = proxy.getClass().getDeclaredField("myInterface");
                // 设置字段可访问（即使是private字段）
                field.setAccessible(true);
                // 将目标对象注入到代理对象的myInterface字段中
                field.set(proxy, this.myInterface);
            } catch (Exception e) {
                // 异常处理：打印堆栈信息
                e.printStackTrace();
            }
        }
    }
}