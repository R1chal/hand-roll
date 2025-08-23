package com.richal.learn;

/**
 * MyInterface接口的具体实现类
 * 
 * 这个类作为动态代理系统的目标对象，实现了MyInterface接口的所有方法。
 * 当使用LogHandler处理器时，代理对象会将方法调用委托给这个类的实例。
 * 
 * 方法实现特点：
 * - 每个方法都打印自己的方法名
 * - 计算并打印方法名的长度
 * - 提供具体的业务逻辑实现
 * 
 * 在动态代理中的作用：
 * 1. 作为被代理的目标对象
 * 2. 提供真实的方法实现
 * 3. 演示代理对象如何委托调用
 * 
 * 设计模式：实现类 + 委托模式
 * 应用场景：作为代理系统的目标对象、演示代理功能等
 */
public class NameAndLengthImpl implements MyInterface {

    /**
     * 实现method1方法
     * 
     * 这个方法演示了：
     * - 获取方法名
     * - 打印方法名
     * - 计算并打印方法名的长度
     * 
     * 输出示例：
     * method1
     * 7
     */
    @Override
    public void method1() {
        String methodName = "method1";
        System.out.println(methodName);
        System.out.println(methodName.length());
    }

    /**
     * 实现method2方法
     * 
     * 这个方法演示了：
     * - 获取方法名
     * - 打印方法名
     * - 计算并打印方法名的长度
     * 
     * 输出示例：
     * method2
     * 7
     */
    @Override
    public void method2() {
        String methodName = "method2";
        System.out.println(methodName);
        System.out.println(methodName.length());
    }

    /**
     * 实现method3方法
     * 
     * 这个方法演示了：
     * - 获取方法名
     * - 打印方法名
     * - 计算并打印方法名的长度
     * 
     * 输出示例：
     * method3
     * 7
     */
    @Override
    public void method3() {
        String methodName = "method3";
        System.out.println(methodName);
        System.out.println(methodName.length());
    }
}
