package com.handnginx.http;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * 第3章：HTTP 请求处理
 * 
 * Nginx HTTP 处理设计思想：
 * 1. 状态机解析 - 用状态机解析 HTTP 请求，而非正则
 * 2. 零拷贝 - 文件直接 sendfile，不经过用户态
 * 3. 分阶段处理 - 11 个阶段，每个阶段由模块处理
 * 
 * 手写版简化：
 * - 解析请求行和请求头
 * - 生成响应
 * - 支持静态文件和反向代理
 */
public class HttpRequest {
    
    // HTTP 方法
    public enum Method { GET, POST, PUT, DELETE, HEAD, OPTIONS }
    
    private Method method;
    private String uri;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    
    // 解析状态
    private boolean parsed = false;
    
    /**
     * 解析 HTTP 请求
     * 
     * Nginx 真实实现：
     * - 状态机逐字节解析
     * - 处理 chunked 编码
     * - 支持 HTTP/1.1 keep-alive
     * 
     * 手写版简化：
     * - 按行解析
     * - 只支持简单请求
     */
    public static HttpRequest parse(ByteBuffer buffer) {
        HttpRequest request = new HttpRequest();
        
        try {
            String data = new String(buffer.array(), 0, buffer.limit());
            String[] lines = data.split("\r\n");
            
            if (lines.length == 0) return request;
            
            // 解析请求行: GET /index.html HTTP/1.1
            String[] requestLine = lines[0].split(" ");
            if (requestLine.length >= 3) {
                request.method = Method.valueOf(requestLine[0]);
                request.uri = requestLine[1];
                request.version = requestLine[2];
            }
            
            // 解析请求头
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) break;
                
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    request.headers.put(key, value);
                }
            }
            
            request.parsed = true;
        } catch (Exception e) {
            System.err.println("[HttpRequest] 解析失败: " + e.getMessage());
        }
        
        return request;
    }
    
    /**
     * 生成 HTTP 响应
     * 
     * Nginx 真实实现：
     * - 分阶段生成响应头
     * - 支持 gzip 压缩
     * - 支持 chunked 传输
     */
    public static byte[] buildResponse(int statusCode, String statusText, 
                                        Map<String, String> headers, byte[] body) {
        StringBuilder response = new StringBuilder();
        
        // 状态行
        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
        
        // 响应头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
            }
        }
        
        // 默认响应头
        response.append("Server: hand-nginx/0.1.0\r\n");
        response.append("Connection: keep-alive\r\n");
        
        if (body != null) {
            response.append("Content-Length: ").append(body.length).append("\r\n");
        }
        
        response.append("\r\n");  // 空行分隔
        
        // 组合响应头和响应体
        byte[] headerBytes = response.toString().getBytes();
        if (body != null) {
            byte[] result = new byte[headerBytes.length + body.length];
            System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
            System.arraycopy(body, 0, result, headerBytes.length, body.length);
            return result;
        }
        
        return headerBytes;
    }
    
    public Method getMethod() { return method; }
    public String getUri() { return uri; }
    public String getVersion() { return version; }
    public Map<String, String> getHeaders() { return headers; }
    public boolean isParsed() { return parsed; }
}
