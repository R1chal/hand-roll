package com.study.netty.bio;

import com.study.netty.common.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 【第一幕】BIO 阻塞式服务器 —— "一个连接一个线程"的天真时代。
 *
 * 运行方式:mvn compile exec:java -Dexec.mainClass=com.study.netty.bio.BioServer
 *
 * 然后用压测客户端打它:
 *   mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="1000 5000"
 *
 * 观察:连接数一多,程序要么 OOM,要么因为线程创建失败直接抛
 *       java.lang.OutOfMemoryError: unable to create native thread
 */
public class BioServer {

    /** 当前已建立的连接数,用来直观感受线程爆炸 */
    private static final AtomicInteger CONNECTION_COUNT = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        // backlog=1024:内核半连接队列长度。
        // 不写的话默认只有 50,压测时几十个连接瞬间涌入会被操作系统直接拒掉,
        // 那是"入口太窄",不是 BIO 真的被打爆 —— 调大它,让对比公平、干净。
        ServerSocket serverSocket = new ServerSocket(Constants.PORT, 1024);
        System.out.println("[BIO] 服务器启动,监听 " + Constants.PORT + " 端口");
        System.out.println("[BIO] 模式:一个连接一个线程。等着被压测打爆吧……\n");

        while (true) {
            // accept() 会阻塞在这里,直到有客户端连进来
            Socket socket = serverSocket.accept();
            int count = CONNECTION_COUNT.incrementAndGet();

            // 每来一个连接,就 new 一个线程去伺候它
            Thread thread = new Thread(() -> handle(socket, count));
            thread.setName("bio-handler-" + count);
            thread.start();

            if (count % 100 == 0) {
                System.out.println("[BIO] 已建立 " + count + " 个连接(也就是 " + count + " 个线程)");
            }
        }
    }

    /**
     * 处理单个连接:整个方法的生命周期都绑定在这个连接上。
     * 客户端不说话,这个线程就死在 readLine() 上,白占着约 1MB 的栈内存。
     */
    private static void handle(Socket socket, int id) {
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String line;
            // readLine() 阻塞:客户端不发数据,线程就在这里一直等
            while ((line = in.readLine()) != null) {
                out.println("服务端已收到: " + line);
            }
        } catch (IOException e) {
            System.out.println("[BIO] 连接 #" + id + " 断开: " + e.getMessage());
        } finally {
            CONNECTION_COUNT.decrementAndGet();
        }
    }
}
