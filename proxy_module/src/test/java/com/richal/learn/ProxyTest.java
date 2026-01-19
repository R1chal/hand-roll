package com.richal.learn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 动态代理系统测试类
 *
 * 测试动态代理系统的完整功能，包括：
 * 1. 不同类型的处理器
 * 2. 代理对象的生成
 * 3. 代理方法的调用
 * 4. 不同的代理行为
 */
public class ProxyTest {

    @Test
    @DisplayName("测试PrintFunctionName处理器 - 简单打印方法名")
    public void testPrintFunctionNameHandler() throws Exception {
        System.out.println("=== 测试PrintFunctionName处理器 ===");

        // 创建简单打印处理器
        MyInterface proxyObject = MyInterfaceFactory.createProxyObject(new PrintFunctionName());

        // 验证代理对象创建成功
        assertNotNull(proxyObject, "代理对象应该成功创建");

        // 调用代理对象的方法
        assertDoesNotThrow(() -> {
            proxyObject.method1();
            proxyObject.method2();
            proxyObject.method3();
        }, "代理方法调用不应该抛出异常");

        System.out.println("PrintFunctionName处理器测试通过");
    }

    @Test
    @DisplayName("测试PrintFunctionName1处理器 - 带前缀打印")
    public void testPrintFunctionName1Handler() throws Exception {
        System.out.println("=== 测试PrintFunctionName1处理器 ===");

        // 创建带前缀打印处理器
        MyInterface proxyObject = MyInterfaceFactory.createProxyObject(new PrintFunctionName1());

        // 验证代理对象创建成功
        assertNotNull(proxyObject, "代理对象应该成功创建");

        // 调用代理对象的方法
        assertDoesNotThrow(() -> {
            proxyObject.method1();
            proxyObject.method2();
            proxyObject.method3();
        }, "代理方法调用不应该抛出异常");

        System.out.println("PrintFunctionName1处理器测试通过");
    }

    @Test
    @DisplayName("测试LogHandler处理器 - AOP日志记录")
    public void testLogHandler() throws Exception {
        System.out.println("=== 测试LogHandler处理器 ===");

        // 创建目标对象
        MyInterface targetObject = new NameAndLengthImpl();

        // 创建日志处理器
        MyInterface proxyObject = MyInterfaceFactory.createProxyObject(new LogHandler(targetObject));

        // 验证代理对象创建成功
        assertNotNull(proxyObject, "代理对象应该成功创建");

        // 捕获输出以验证日志功能
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // 调用代理对象的方法
            proxyObject.method1();

            // 恢复标准输出
            System.setOut(originalOut);

            // 验证输出包含before和after日志
            String output = outputStream.toString();
            assertTrue(output.contains("before"), "输出应该包含'before'日志");
            assertTrue(output.contains("after"), "输出应该包含'after'日志");

            System.out.println("LogHandler处理器测试通过");
            System.out.println("捕获的输出: " + output);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("测试完整的代理流程 - 所有处理器")
    public void testCompleteProxyFlow() throws Exception {
        System.out.println("=== 测试完整的代理流程 ===");

        // 第一步：测试简单打印处理器
        System.out.println("--- 使用PrintFunctionName处理器 ---");
        MyInterface proxyObject = MyInterfaceFactory.createProxyObject(new PrintFunctionName());
        proxyObject.method1();
        proxyObject.method2();
        proxyObject.method3();

        // 第二步：测试带前缀打印处理器
        System.out.println("\n--- 使用PrintFunctionName1处理器 ---");
        proxyObject = MyInterfaceFactory.createProxyObject(new PrintFunctionName1());
        proxyObject.method1();
        proxyObject.method2();
        proxyObject.method3();

        // 第三步：测试日志处理器
        System.out.println("\n--- 使用LogHandler处理器 ---");
        MyInterface targetObject = new NameAndLengthImpl();
        proxyObject = MyInterfaceFactory.createProxyObject(new LogHandler(targetObject));
        proxyObject.method1();
        proxyObject.method2();
        proxyObject.method3();

        System.out.println("\n完整代理流程测试通过");
    }

    /**
     * 简单打印处理器（内部类，用于测试）
     */
    static class PrintFunctionName implements MyHandler {
        @Override
        public String functionBody(String methodName) {
            return " System.out.println(\"" + methodName + "\");";
        }

        @Override
        public void setProxy(MyInterface proxy) {
            // 简单处理器不需要特殊处理
        }
    }

    /**
     * 带前缀打印处理器（内部类，用于测试）
     */
    static class PrintFunctionName1 implements MyHandler {
        @Override
        public String functionBody(String methodName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" System.out.println(1);");
            stringBuilder.append(" System.out.println(\"" + methodName + "\");");
            return stringBuilder.toString();
        }

        @Override
        public void setProxy(MyInterface proxy) {
            // 简单处理器不需要特殊处理
        }
    }

    /**
     * 日志记录处理器（内部类，用于测试）
     */
    static class LogHandler implements MyHandler {
        private MyInterface myInterface;

        public LogHandler(MyInterface myInterface) {
            this.myInterface = myInterface;
        }

        @Override
        public String functionBody(String methodName) {
            String context = " System.out.println(\"before\");\n" +
                             " this.myInterface." + methodName + "();\n" +
                             " System.out.println(\"after\");";
            return context;
        }

        @Override
        public void setProxy(MyInterface proxy) {
            try {
                Field field = proxy.getClass().getDeclaredField("myInterface");
                field.setAccessible(true);
                field.set(proxy, this.myInterface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
