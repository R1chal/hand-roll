package com.richal.learn.web;

import com.richal.learn.Autowired;
import com.richal.learn.Component;
import com.richal.learn.PostConstruct;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.logging.LogManager;

/**
 * 内嵌 Tomcat 启动器。
 * 为什么内嵌：开发/演示时“开箱即用”，无需额外安装/部署应用服务器。
 */
@Component
public class TomCatServer {

    @Autowired
    DispatcherServlet dispatcherServlet;

    @PostConstruct
    public void start() throws LifecycleException {
        // 为什么桥接 JUL→SLF4J：Tomcat 内部大量使用 JUL，桥接后统一日志门面，便于过滤与收集
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        int port = 8081;
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        String contextPath = "";
        String docBasePath = new File(".").getAbsolutePath();
        Context ctx = tomcat.addContext(contextPath, docBasePath);

        // 注册自定义前端控制器为默认 Servlet：演示目的下接管所有请求
        tomcat.addServlet(contextPath, "dispatcherServlet", dispatcherServlet);

        // “/*” 映射：统一入口，便于观察完整链路
        ctx.addServletMappingDecoded("/*", "dispatcherServlet");
        tomcat.start();
        System.out.println("Tomcat Started at port: " + port);
    }
}
