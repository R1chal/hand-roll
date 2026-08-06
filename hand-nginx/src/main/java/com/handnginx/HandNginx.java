package com.handnginx;

import com.handnginx.config.NginxConfig;
import com.handnginx.event.EventLoop;

/**
 * 第1章：main() 程序入口
 * 
 * Nginx 设计思想：
 * 1. 配置驱动 - 所有行为由配置文件决定
 * 2. 模块化 - 核心流程固定，功能由模块实现
 * 3. 单进程启动 - master 进程 fork 出 worker 进程
 * 
 * 手写版简化：
 * - 单进程模型（不 fork worker）
 * - 配置驱动行为
 * - 事件循环驱动
 */
public class HandNginx {
    
    // Nginx 版本信息
    public static final String VERSION = "hand-nginx/0.1.0";
    
    public static void main(String[] args) {
        System.out.println("[HandNginx] 启动 " + VERSION);
        
        // 1. 解析配置文件（第2章）
        String configPath = args.length > 0 ? args[0] : "conf/nginx.conf";
        NginxConfig config = NginxConfig.parse(configPath);
        System.out.println("[HandNginx] 配置加载完成，监听端口: " + config.getListenPort());
        
        // 2. 启动事件循环（第6章）
        // Nginx 核心：事件驱动，所有 I/O 都是异步非阻塞
        EventLoop eventLoop = new EventLoop(config);
        
        // 3. 进入事件循环，永不退出
        // 这是 Nginx 的核心：一个循环处理所有连接
        eventLoop.run();
    }
}
