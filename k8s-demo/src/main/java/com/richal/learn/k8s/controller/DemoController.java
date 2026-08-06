package com.richal.learn.k8s.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 演示用的 REST API 控制器
 */
@RestController
public class DemoController {

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 打招呼接口
     */
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello from K8s!");
        result.put("podName", getPodName());
        result.put("hostName", getHostName());
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 获取 Pod 信息
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("app", "hand-roll-k8s-demo");
        result.put("version", "1.0.0");
        result.put("javaVersion", System.getProperty("java.version"));
        result.put("osName", System.getProperty("os.name"));
        result.put("podName", getPodName());
        result.put("hostName", getHostName());
        return result;
    }

    private String getPodName() {
        String podName = System.getenv("HOSTNAME");
        return podName != null ? podName : "unknown";
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
