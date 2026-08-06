package com.handnginx.config;

import java.io.*;
import java.util.*;

/**
 * 第2章：配置文件（nginx.conf）是如何解析的
 * 
 * Nginx 配置设计思想：
 * 1. 层级结构 - http/server/location 嵌套
 * 2. 指令驱动 - 每个指令对应一个处理函数
 * 3. 配置即代码 - 配置文件决定所有行为
 * 
 * 手写版简化：
 * - 用 Properties 模拟层级配置
 * - 支持 listen/server_name/location/proxy_pass 等核心指令
 */
public class NginxConfig {
    
    private int listenPort = 8080;
    private String serverName = "localhost";
    private String root = "/var/www/html";
    private int workerProcesses = 1;
    
    // location 配置：路径 -> 处理方式
    // Nginx 核心：location 匹配决定请求如何处理
    private Map<String, LocationConfig> locations = new LinkedHashMap<>();
    
    /**
     * 解析配置文件
     * 
     * Nginx 真实实现：
     * - 使用状态机解析配置文件
     * - 每个指令有对应的 handler
     * - 配置解析后生成配置树
     * 
     * 手写版简化：
     * - 用 Properties 格式
     * - 支持核心指令
     */
    public static NginxConfig parse(String path) {
        NginxConfig config = new NginxConfig();
        
        // 模拟解析 nginx.conf
        // 真实 Nginx 用状态机逐字符解析
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            String currentLocation = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                // 解析 listen 指令
                if (line.startsWith("listen")) {
                    config.listenPort = Integer.parseInt(
                        line.split("\\s+")[1].replace(";", ""));
                }
                // 解析 server_name 指令
                else if (line.startsWith("server_name")) {
                    config.serverName = line.split("\\s+")[1].replace(";", "");
                }
                // 解析 root 指令
                else if (line.startsWith("root")) {
                    config.root = line.split("\\s+")[1].replace(";", "");
                }
                // 解析 location 块
                else if (line.startsWith("location")) {
                    currentLocation = line.split("\\s+")[1].replace("{", "").trim();
                    config.locations.put(currentLocation, new LocationConfig());
                }
                // 解析 location 内的 proxy_pass
                else if (line.startsWith("proxy_pass") && currentLocation != null) {
                    String proxyUrl = line.split("\\s+")[1].replace(";", "");
                    config.locations.get(currentLocation).setProxyPass(proxyUrl);
                }
                // 解析 location 内的 root
                else if (line.startsWith("root") && currentLocation != null) {
                    String rootPath = line.split("\\s+")[1].replace(";", "");
                    config.locations.get(currentLocation).setRoot(rootPath);
                }
                // 结束 location 块
                else if (line.equals("}")) {
                    currentLocation = null;
                }
            }
        } catch (IOException e) {
            System.out.println("[Config] 配置文件不存在，使用默认配置");
            // 默认配置
            config.locations.put("/", new LocationConfig().setRoot("/var/www/html"));
        }
        
        return config;
    }
    
    public int getListenPort() { return listenPort; }
    public String getServerName() { return serverName; }
    public String getRoot() { return root; }
    public Map<String, LocationConfig> getLocations() { return locations; }
    
    /**
     * Location 配置
     */
    public static class LocationConfig {
        private String root;
        private String proxyPass;
        private String alias;
        
        public String getRoot() { return root; }
        public LocationConfig setRoot(String root) { this.root = root; return this; }
        public String getProxyPass() { return proxyPass; }
        public LocationConfig setProxyPass(String proxyPass) { this.proxyPass = proxyPass; return this; }
        public String getAlias() { return alias; }
        public LocationConfig setAlias(String alias) { this.alias = alias; return this; }
        
        public boolean isProxy() {
            return proxyPass != null && !proxyPass.isEmpty();
        }
    }
}
