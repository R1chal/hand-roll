package com.richal.learn;

import java.io.IOException;

/**
 * 🚀 Main - MiniSpring应用启动入口
 * 
 * 📋 作用：测试和演示MiniSpring IOC容器
 * 🔍 流程：
 * 1. 启动ApplicationContext容器
 * 2. 扫描指定包路径
 * 3. 初始化@Component标记的Bean
 * 4. 管理Bean生命周期
 * 
 * 💡 Spring应用启动流程：
 * - 容器启动：创建ApplicationContext实例
 * - 包扫描：发现所有@Component标记的类
 * - Bean注册：将类信息注册为BeanDefinition
 * - 实例化：按需创建Bean实例
 * - 依赖注入：自动注入@Autowired标记的依赖
 * - 初始化：执行@PostConstruct方法
 */
public class Main {
    
    /**
     * 🎯 应用主入口
     * 📋 功能：启动ApplicationContext容器
     * 🔍 流程：
     * 1. 创建容器实例
     * 2. 触发初始化流程
     * 3. 扫描包并注册Bean
     * 4. 创建并初始化Bean
     * @param args 命令行参数（未使用）
     * @throws IOException 包扫描异常
     */
    public static void main(String[] args) throws IOException {
        // 🚀 启动Spring IOC容器，开始Bean管理之旅
        new ApplicationContext("com.richal.learn");
        
        // 🎉 容器启动完成！
        // 此时所有@Component标记的Bean已完成扫描、注册和初始化
        
        // 💭 可在此添加测试代码验证容器功能...
    }
}