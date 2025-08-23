package com.richal.learn;

import javax.tools.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 动态代理的编译器
 * 
 * 这个类负责将动态生成的Java源代码编译成字节码(.class文件)。
 * 它是整个动态代理系统的重要组成部分，实现了运行时编译的功能。
 * 
 * 核心功能：
 * 1. 接收生成的Java源文件
 * 2. 使用系统Java编译器进行编译
 * 3. 将编译结果输出到指定目录
 * 
 * 技术特点：
 * - 使用Java 6引入的javax.tools包
 * - 支持运行时编译
 * - 自动管理编译输出路径
 * 
 * 设计模式：外观模式 + 策略模式
 * 应用场景：动态代码生成、热部署、插件系统等
 */
public class Compiler {
    
    /**
     * 编译Java源文件
     * 
     * 这个方法使用系统Java编译器将Java源文件编译成字节码。
     * 编译过程包括：
     * 1. 获取系统Java编译器实例
     * 2. 创建标准文件管理器
     * 3. 设置编译选项（输出目录）
     * 4. 执行编译任务
     * 5. 处理编译结果
     * 
     * 编译流程：
     * 1. 检查系统是否有可用的Java编译器
     * 2. 设置输出目录为 target/classes
     * 3. 创建编译任务并执行
     * 4. 输出编译结果信息
     * 
     * @param javaFile 需要编译的Java源文件
     * 
     * 注意事项：
     * - 确保系统安装了JDK（不仅仅是JRE）
     * - 编译输出目录会自动创建
     * - 编译失败时会输出详细错误信息
     * 
     * 异常处理：
     * - 捕获编译过程中的所有异常
     * - 输出详细的错误信息和堆栈跟踪
     * - 确保程序不会因为编译失败而崩溃
     */
    public static void compile(File javaFile) {
        // 获取系统 Java 编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        
        // 检查是否有可用的编译器
        if (compiler == null) {
            System.err.println("错误：系统没有可用的Java编译器，请确保安装了JDK");
            return;
        }
        
        // 创建标准文件管理器
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            
            // 获取源文件对象
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile));
            
            // 设置编译选项，输出到 target/classes 目录
            // 使用绝对路径确保编译输出位置正确
            String outputDir = new File("target/classes").getAbsolutePath();
            List<String> options = Arrays.asList("-d", outputDir);
            
            // 创建编译任务
            JavaCompiler.CompilationTask task = compiler.getTask(
                null,               // 输出流（null表示使用默认输出）
                fileManager,        // 文件管理器
                null,               // 诊断监听器（null表示使用默认诊断）
                options,            // 编译选项（-d指定输出目录）
                null,               // 注解处理器（null表示不使用）
                compilationUnits    // 编译单元（要编译的源文件）
            );
            
            // 执行编译
            boolean success = task.call();
            
            if (success) {
                System.out.println("编译成功!");
            } else {
                System.out.println("编译失败!");
            }
        } catch (Exception e) {
            System.err.println("编译过程中发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
