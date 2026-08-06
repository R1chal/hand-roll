package com.handnginx.event;

import com.handnginx.config.NginxConfig;
import com.handnginx.connection.Connection;
import com.handnginx.http.HttpRequest;
import com.handnginx.location.LocationMatcher;
import com.handnginx.proxy.ReverseProxy;
import com.handnginx.config.NginxConfig.LocationConfig;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 第6章：事件驱动循环 —— epoll 为什么能支撑几十万连接
 * 
 * Nginx 事件驱动设计思想：
 * 1. 单线程事件循环 - 一个线程处理所有连接
 * 2. 非阻塞 I/O - 不等待，事件通知
 * 3. 边缘触发 - epoll ET 模式，减少系统调用
 * 4. 连接复用 - keep-alive 复用连接
 * 
 * Java 版实现：
 * - 用 Selector 替代 epoll（Java NIO）
 * - 单线程 Selector 循环
 * - 线程池处理业务逻辑
 * - 非阻塞 I/O
 */
public class EventLoop {
    
    private NginxConfig config;
    private LocationMatcher locationMatcher;
    private ExecutorService workerPool;
    
    // Selector 替代 epoll
    private Selector selector;
    private ServerSocketChannel serverChannel;
    
    // 连接管理
    private Map<SocketChannel, Connection> connections = new ConcurrentHashMap<>();
    
    public EventLoop(NginxConfig config) {
        this.config = config;
        this.locationMatcher = new LocationMatcher(config.getLocations());
        // 线程池处理业务逻辑（模拟 Nginx worker 进程）
        this.workerPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2);
    }
    
    /**
     * 启动事件循环
     * 
     * Nginx 真实实现：
     * - 创建 epoll 实例
     * - 注册监听 socket
     * - 循环 epoll_wait
     * - 处理就绪事件
     * 
     * Java 版：
     * - Selector.open()
     * - serverChannel.register()
     * - selector.select()
     * - 处理 SelectionKey
     */
    public void run() {
        try {
            // 1. 创建 Selector（替代 epoll_create）
            selector = Selector.open();
            
            // 2. 创建监听 socket（替代 socket + bind + listen）
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);  // 非阻塞模式！
            serverChannel.bind(new InetSocketAddress(config.getListenPort()));
            
            // 3. 注册到 Selector（替代 epoll_ctl ADD）
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            System.out.println("[EventLoop] 监听端口 " + config.getListenPort() + "，等待连接...");
            
            // 4. 事件循环（替代 epoll_wait 循环）
            while (true) {
                // select() 替代 epoll_wait
                // 阻塞等待事件，但只阻塞在这里，不阻塞 I/O
                int readyCount = selector.select();
                
                if (readyCount == 0) continue;
                
                // 处理就绪的 channel
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    
                    if (key.isAcceptable()) {
                        handleAccept(key);  // 新连接
                    } else if (key.isReadable()) {
                        handleRead(key);     // 可读事件
                    } else if (key.isWritable()) {
                        handleWrite(key);    // 可写事件
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("[EventLoop] 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 处理新连接
     * 
     * Nginx 真实实现：
     * - accept 新连接
     * - 设置非阻塞
     * - 注册到 epoll
     * - 初始化连接结构
     */
    private void handleAccept(SelectionKey key) throws Exception {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();  // 非阻塞，立即返回
        
        if (client == null) return;
        
        client.configureBlocking(false);  // 非阻塞！
        
        // 注册读事件（替代 epoll_ctl ADD + EPOLLIN）
        client.register(selector, SelectionKey.OP_READ);
        
        Connection conn = new Connection(client);
        connections.put(client, conn);
        
        System.out.println("[Accept] 新连接: " + client.getRemoteAddress());
    }
    
    /**
     * 处理读事件
     * 
     * Nginx 真实实现：
     * - 非阻塞 read
     * - 解析 HTTP 请求
     * - 状态机处理
     * - 注册写事件
     */
    private void handleRead(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        Connection conn = connections.get(client);
        
        if (conn == null) return;
        
        try {
            int bytesRead = conn.read();
            
            if (bytesRead == -1) {
                // 连接关闭
                conn.close();
                connections.remove(client);
                key.cancel();
                return;
            }
            
            if (bytesRead > 0) {
                // 解析 HTTP 请求
                HttpRequest request = HttpRequest.parse(conn.getReadBuffer());
                
                if (request.isParsed()) {
                    System.out.println("[Read] " + request.getMethod() + " " + request.getUri());
                    
                    // 提交到线程池处理（Nginx 是在当前 worker 处理）
                    workerPool.submit(() -> processRequest(conn, request, key));
                }
            }
            
        } catch (Exception e) {
            System.err.println("[Read] 错误: " + e.getMessage());
            conn.close();
            connections.remove(client);
            key.cancel();
        }
    }
    
    /**
     * 处理 HTTP 请求
     */
    private void processRequest(Connection conn, HttpRequest request, SelectionKey key) {
        try {
            String uri = request.getUri();
            
            // 1. location 匹配
            LocationMatcher.MatchResult match = locationMatcher.match(uri);
            
            byte[] response;
            
            if (match != null && match.getConfig().isProxy()) {
                // 2. 反向代理
                ReverseProxy proxy = new ReverseProxy(match.getConfig().getProxyPass());
                response = proxy.forward(
                    request.getMethod().name(),
                    uri,
                    request.getHeaders(),
                    null
                );
            } else {
                // 3. 静态文件服务
                response = serveStaticFile(uri, match);
            }
            
            // 4. 准备响应
            conn.getWriteBuffer().clear();
            conn.getWriteBuffer().put(response);
            conn.getWriteBuffer().flip();
            
            // 5. 注册写事件（替代 epoll_ctl MOD + EPOLLOUT）
            key.interestOps(SelectionKey.OP_WRITE);
            
        } catch (Exception e) {
            System.err.println("[Process] 处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理写事件
     */
    private void handleWrite(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        Connection conn = connections.get(client);
        
        if (conn == null) return;
        
        try {
            int bytesWritten = conn.write();
            
            if (!conn.getWriteBuffer().hasRemaining()) {
                // 写完了，重新注册读事件（keep-alive）
                key.interestOps(SelectionKey.OP_READ);
            }
            
        } catch (Exception e) {
            System.err.println("[Write] 错误: " + e.getMessage());
            conn.close();
            connections.remove(client);
            key.cancel();
        }
    }
    
    /**
     * 静态文件服务
     */
    private byte[] serveStaticFile(String uri, LocationMatcher.MatchResult match) {
        try {
            String root = config.getRoot();
            if (match != null && match.getConfig().getRoot() != null) {
                root = match.getConfig().getRoot();
            }
            
            String path = root + uri;
            if (path.endsWith("/")) {
                path += "index.html";
            }
            
            File file = new File(path);
            if (!file.exists()) {
                return buildErrorResponse(404, "Not Found", "文件不存在: " + uri);
            }
            
            // 读取文件
            byte[] content = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(content);
            }
            
            // 确定 Content-Type
            String contentType = guessContentType(path);
            
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", contentType);
            
            return HttpRequest.buildResponse(200, "OK", headers, content);
            
        } catch (Exception e) {
            return buildErrorResponse(500, "Internal Server Error", e.getMessage());
        }
    }
    
    private String guessContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain";
    }
    
    private byte[] buildErrorResponse(int status, String statusText, String message) {
        String body = "<html><body><h1>" + status + " " + statusText + "</h1><p>" + message + "</p></body></html>";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html");
        return HttpRequest.buildResponse(status, statusText, headers, body.getBytes());
    }
}
