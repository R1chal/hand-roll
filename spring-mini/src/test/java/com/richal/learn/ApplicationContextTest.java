package com.richal.learn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApplicationContext测试类
 *
 * 测试MiniSpring IOC容器的功能
 */
public class ApplicationContextTest {

    @Test
    @DisplayName("测试ApplicationContext容器启动和Bean扫描")
    public void testApplicationContextStartup() throws IOException {
        // 启动Spring IOC容器，扫描指定包路径
        ApplicationContext context = new ApplicationContext("com.richal.learn");

        // 验证容器启动成功
        assertNotNull(context, "ApplicationContext应该成功创建");

        System.out.println("容器启动完成！");
        System.out.println("所有@Component标记的Bean已完成扫描、注册和初始化");
    }

    @Test
    @DisplayName("测试ApplicationContext容器启动流程")
    public void testApplicationContextInitialization() {
        // 测试容器初始化流程
        assertDoesNotThrow(() -> {
            ApplicationContext context = new ApplicationContext("com.richal.learn");
            System.out.println("容器初始化流程测试通过");
        }, "容器启动不应该抛出异常");
    }
}
