package com.handnginx.connection;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 第3章：HTTP 请求是如何被接收的 —— 连接管理
 * 
 * Nginx 连接设计思想：
 * 1. 连接复用 - 一个连接处理多个请求（HTTP keep-alive）
 * 2. 连接池 - 预分配连接，减少创建开销
 * 3. 非阻塞 I/O - 不等待，事件通知
 * 
 * 手写版简化：
 * - 封装 SocketChannel
 * - 管理读写缓冲区
 * - 记录连接状态
 */
public class Connection {
    
    // 连接状态（Nginx 用状态机管理）
    public enum State {
        READING,      // 读取请求
        PROCESSING,   // 处理请求
        WRITING,      // 写入响应
        CLOSED        // 连接关闭
    }
    
    private SocketChannel channel;
    private State state;
    private long createTime;
    
    // 读写缓冲区（Nginx 用内存池管理）
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    
    // 请求信息
    private String remoteAddr;
    private int remotePort;
    
    public Connection(SocketChannel channel) {
        this.channel = channel;
        this.state = State.READING;
        this.createTime = System.currentTimeMillis();
        this.readBuffer = ByteBuffer.allocate(4096);  // 4KB 读缓冲区
        this.writeBuffer = ByteBuffer.allocate(4096); // 4KB 写缓冲区
        
        try {
            this.remoteAddr = channel.getRemoteAddress().toString();
        } catch (Exception e) {
            this.remoteAddr = "unknown";
        }
    }
    
    public SocketChannel getChannel() { return channel; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public ByteBuffer getReadBuffer() { return readBuffer; }
    public ByteBuffer getWriteBuffer() { return writeBuffer; }
    public String getRemoteAddr() { return remoteAddr; }
    
    /**
     * 读取数据到缓冲区
     * 
     * Nginx 真实实现：
     * - 使用非阻塞 read
     * - 配合 epoll 事件通知
     * - 内存池分配缓冲区
     */
    public int read() throws Exception {
        readBuffer.clear();
        int bytesRead = channel.read(readBuffer);
        readBuffer.flip();
        return bytesRead;
    }
    
    /**
     * 写入响应数据
     * 
     * Nginx 真实实现：
     * - 非阻塞 write
     * - 写不完则注册写事件
     * - 使用 sendfile 零拷贝
     */
    public int write() throws Exception {
        return channel.write(writeBuffer);
    }
    
    /**
     * 关闭连接
     */
    public void close() {
        try {
            state = State.CLOSED;
            channel.close();
        } catch (Exception e) {
            // ignore
        }
    }
    
    public boolean isClosed() {
        return state == State.CLOSED;
    }
}
