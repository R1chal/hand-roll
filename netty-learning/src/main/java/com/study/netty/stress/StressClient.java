package com.study.netty.stress;

import com.study.netty.common.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 压测客户端 —— 用来分别"虐待"三个版本的服务器。
 *
 * 用法:
 *   mvn compile exec:java -Dexec.mainClass=com.study.netty.stress.StressClient \
 *       -Dexec.args="<连接数> <每连接发送消息数>"
 *
 * 两种经典玩法:
 *
 *  【玩法 A · 打爆 BIO】大量空闲连接,看线程爆炸
 *     mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="3000 0"
 *     → 建立 3000 个连接但不发消息,全部挂着。
 *       BIO 版会创建 3000 个线程,内存暴涨甚至 OOM;Netty 版毫无压力。
 *
 *  【玩法 B · 打出粘包/拆包】少量连接疯狂连发
 *     mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="1 10"
 *     → 1 个连接连发 10 条消息。
 *       NIO 版会看到消息粘在一起或被切碎;Netty 版一条是一条。
 */
public class StressClient {

    public static void main(String[] args) throws InterruptedException {
        int connections = args.length > 0 ? Integer.parseInt(args[0]) : 100;
        int msgsPerConn = args.length > 1 ? Integer.parseInt(args[1]) : 5;

        System.out.println("[压测] 准备建立 " + connections + " 个连接,每个连接发 " + msgsPerConn + " 条消息");
        long start = System.currentTimeMillis();

        AtomicInteger connected = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);
        CountDownLatch done = new CountDownLatch(connections);
        List<Socket> keepAlive = new ArrayList<>();  // 防止 Socket 被 GC 关掉

        for (int i = 0; i < connections; i++) {
            final int id = i;
            // 🚀 关键:用虚拟线程(Java 21+),而不是平台线程。
            //    平台线程一个约 1MB 栈、几千个就到系统上限 —— 这就是你上次卡在 2500 的原因。
            //    虚拟线程几乎不占内存,一个进程轻松起几万、几十万个,
            //    客户端自己先不崩,才能把压力真正灌给服务端。
            Thread.ofVirtual().start(() -> {
                try {
                    Socket socket = new Socket(Constants.HOST, Constants.PORT);
                    synchronized (keepAlive) {
                        keepAlive.add(socket);
                    }
                    connected.incrementAndGet();

                    // 连发消息:消息之间几乎无间隔,最容易触发粘包
                    OutputStream out = socket.getOutputStream();
                    for (int m = 0; m < msgsPerConn; m++) {
                        String msg = "conn-" + id + "-msg-" + m + "\n";
                        out.write(msg.getBytes(StandardCharsets.UTF_8));
                    }
                    out.flush();

                    // 挂着不退出,让 BIO 版一直占着线程
                    Thread.sleep(30_000);
                } catch (IOException | InterruptedException e) {
                    failed.incrementAndGet();
                    // 打爆服务器的第一现场 —— 别静默吞掉,看看到底是哪种失败。
                    // Connection refused(队列满被拒) / SocketException(对端重置) 含义完全不同。
                    System.out.println("[压测] ❌ 连接 #" + id + " 失败: " + e.getClass().getSimpleName()
                            + " - " + e.getMessage());
                } finally {
                    done.countDown();
                }
            });
        }

        done.await();
        long cost = System.currentTimeMillis() - start;
        System.out.println("\n[压测] 结束:成功 " + connected.get() + " 个连接,失败 " + failed.get()
                + " 个,耗时 " + cost + " ms");
        if (failed.get() > 0) {
            System.out.println("[压测] ⚠️ 有连接失败了 —— 服务器很可能已经扛不住了,去看看它的控制台/内存");
        }
    }
}
