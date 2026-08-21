package com.study.netty.netty;

import com.study.netty.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 【第四幕】Netty —— 雨过天晴。
 *
 * 运行方式:mvn compile exec:java -Dexec.mainClass=com.study.netty.netty.NettyServer
 *
 * 用和打 NIO 版一模一样的压测命令打它:
 *   mvn exec:java -Dexec.mainClass=com.study.netty.stress.StressClient -Dexec.args="1 10"
 *
 * 对比观察:消息一条条整整齐齐,再也不会粘连或切碎。
 *
 * 对照 NioServer 看 —— 你在那里踩过的每一个坑,这里都是怎么消失的:
 *   粘包/拆包状态机   → 一行 LineBasedFrameDecoder
 *   Selector/事件循环 → EventLoopGroup 替你管好
 *   ByteBuffer flip  → ByteBuf 读写索引分离,你不用碰
 *   epoll 空轮询 bug  → Netty 内部检测并重建 Selector,自动规避
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        // boss:只负责接受新连接,1 个线程足够
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        // worker:负责所有连接的读写。不传参数,默认线程数 = CPU核数 × 2。
        //         上万连接也只靠这几个线程 —— 这就是和 BIO 的本质区别。
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    // 和 BioServer 的 backlog=1024 对齐:内核半连接队列,
                    // 防止压测时瞬间涌入的连接被操作系统拒掉(那不是性能问题,是入口太窄)。
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                              // ✨ 粘包/拆包?一行解码器的事。
                              //    按 \n 切分,单条超 1024 字节则丢弃,防止恶意攻击撑爆内存。
                              .addLast(new LineBasedFrameDecoder(1024))
                              // 字节 → 字符串
                              .addLast(new StringDecoder(CharsetUtil.UTF_8))
                              // 字符串 → 字节(用于写回)
                              .addLast(new StringEncoder(CharsetUtil.UTF_8))
                              // 👇 你的业务代码,只剩这一块。前面全是框架替你扛的。
                              .addLast(new SimpleChannelInboundHandler<String>() {
                                  @Override
                                  protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                      // msg 已经是一条完整、干净的消息 —— 没有半包,没有粘连
                                      System.out.println("[Netty] 收到一条完整消息 → [" + msg + "]");
                                      ctx.writeAndFlush("服务端已收到: " + msg + "\n");
                                  }

                                  @Override
                                  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                      // 连接异常、解码失败……统一在这里兜底
                                      ctx.close();
                                  }
                              });
                        }
                    })
                    .bind(Constants.PORT).sync();

            System.out.println("[Netty] 服务器启动,监听 " + Constants.PORT + " 端口");
            System.out.println("[Netty] worker 线程数 = CPU核数×2,连接数随便上万。\n");

        } finally {
            // 优雅关闭(本例是长驻服务,main 阻塞在 bind 上,实际走不到)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }));
        }
    }
}
