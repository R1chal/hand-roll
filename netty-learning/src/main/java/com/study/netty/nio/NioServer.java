package com.study.netty.nio;

import com.study.netty.common.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 【第三幕】手写原生 NIO —— 真正的心理阴影。
 *
 * 运行方式:mvn compile exec:java -Dexec.mainClass=com.study.netty.nio.NioServer
 *
 * 这个类故意【没有】处理粘包/拆包 —— 让你亲眼看到消息边界丢失的混乱。
 * 用压测客户端的连续发送模式打它:
 *   mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="1 10"
 * 观察控制台:多条消息会粘在一起,或者被切成碎片。
 *
 * 然后自己数一数代码里有多少个 ⚠️ 标注的"经典天坑"。
 */
public class NioServer {

    public static void main(String[] args) throws IOException {
        // 1. 打开选择器:NIO 的灵魂,一个线程监听所有通道的事件
        Selector selector = Selector.open();

        // 2. 打开服务器通道并设置为非阻塞
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(Constants.PORT));
        serverChannel.configureBlocking(false);

        // 3. 把"接受连接"这个事件注册到选择器上
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("[NIO] 服务器启动,单线程 Reactor,监听 " + Constants.PORT + " 端口");
        System.out.println("[NIO] 注意:本版本故意不处理粘包/拆包,准备好面对混乱……\n");

        while (true) {
            // select() 阻塞,直到至少有一个事件就绪。
            // ⚠️ 天坑预警:Linux 上 JDK 的 epoll bug 可能让这里空转,
            //    select() 立即返回但一个事件都没有,导致 while(true) 100% 占满 CPU。
            selector.select();

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();

                // ⚠️ 天坑 1:必须 remove,否则同一个事件会被反复处理
                it.remove();

                try {
                    if (key.isAcceptable()) {
                        handleAccept(selector, serverChannel);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                } catch (IOException e) {
                    // 客户端突然断开连接
                    key.cancel();
                    key.channel().close();
                    System.out.println("[NIO] 连接异常断开: " + e.getMessage());
                }
            }
        }
    }

    /** 有新连接进来 */
    private static void handleAccept(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("[NIO] 新连接: " + client.getRemoteAddress());
    }

    /** 有数据可读 —— 这里藏着最多坑 */
    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        // ⚠️ 天坑 2:固定大小的缓冲区。消息比 1024 字节大怎么办?会被切断。
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len = client.read(buffer);

        if (len == -1) {
            // ⚠️ 天坑 3:客户端正常关闭时会读到 -1,不处理就会死循环
            client.close();
            return;
        }

        // ⚠️ 天坑 4:ByteBuffer 读写模式切换。忘了 flip() 会读出一堆空气/旧数据
        buffer.flip();
        String msg = StandardCharsets.UTF_8.decode(buffer).toString();

        // 💔 重点观察这里:客户端连续发的 "msg-1\nmsg-2\nmsg-3\n",
        //    你在这里看到的可能是 "msg-1\nmsg-2\nmsg-" (拆包)
        //    或者三条全粘在一起一口气冒出来 (粘包)。
        //    TCP 是字节流,根本不知道"一条消息"在哪结束!
        System.out.println("[NIO] 原始读到 → [" + msg.replace("\n", "\\n") + "]");

        // ⚠️ 天坑 5:没处理"读了一半的数据"。剩下半条消息直接丢了,下次也接不上。
        // 要正确处理,你得给每个连接维护一个"读了一半的残包缓冲",
        // 自己做协议解析状态机 —— 代码量瞬间翻好几倍,而且全是和业务无关的体力活。
    }
}
