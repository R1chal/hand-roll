package com.study.netty.ws.chat;

import com.study.netty.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.TimeUnit;

/**
 * 【第五幕】Netty WebSocket 聊天室 —— 前面手写原生吃的苦,这里全消失。
 *
 * 运行方式:mvn compile exec:java -Dexec.mainClass=com.study.netty.ws.chat.WsChatServer
 *
 * 双击打开 web/chat.html,地址填 ws://127.0.0.1:8888/ws,多开几个标签页互发消息。
 *
 * 对照 RawWebSocketServer 看你被拯救了什么:
 *   HTTP Upgrade 握手 + SHA-1/Base64  → WebSocketServerProtocolHandler 替你完成
 *   帧头按位解析 / 变长长度 / unmask    → 内置 WebSocketFrameDecoder 全自动
 *   分片重组、ping/pong、close         → 全自动,你只收干净的 TextWebSocketFrame
 *   粘包/拆包                           → 帧边界由协议保证,框架替你切好
 *
 * 你的业务代码只剩最后一个 Handler 那几行。
 */
public class WsChatServer {

    /** 所有在线连接。ChannelGroup 是线程安全的容器,广播一句话的事。 */
    private static final ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);     // 只 accept 新连接
        NioEventLoopGroup worker = new NioEventLoopGroup();    // 所有连接的读写,CPU核数×2

        try {
            new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                              // 1) WebSocket 握手是 HTTP 请求,先得有 HTTP 编解码
                              .addLast(new HttpServerCodec())
                              // 2) 把分片的 HTTP 聚合成完整请求(握手前用)
                              .addLast(new HttpObjectAggregator(65536))
                              // 3) 心跳:5分钟没读到数据才触发一次"读空闲"事件,交给业务处理
                              .addLast(new IdleStateHandler(300, 0, 0, TimeUnit.SECONDS))
                              // 4) ✨ 灵魂所在:自动完成握手,此后 pipeline 里流的就是干净的 WebSocket 帧
                              //    它还自动处理 Ping(回 Pong)、Pong、Close —— 全不用你管
                              .addLast(new WebSocketServerProtocolHandler("/ws", null, true))
                              // 5) 👇 你的业务:收文本帧,广播给所有人
                              .addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                                  @Override
                                  public void channelActive(ChannelHandlerContext ctx) {
                                      // 连接建立,加入群组
                                      CLIENTS.add(ctx.channel());
                                      System.out.println("[聊天室] 有人上线,当前在线 " + CLIENTS.size() + " 人");
                                  }

                                  @Override
                                  protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
                                      String msg = frame.text();
                                      String id = shortId(ctx.channel());
                                      System.out.println("[聊天室] " + id + " 说: " + msg);
                                      // 一行广播给所有在线连接(不用自己遍历+判活,框架包了)
                                      CLIENTS.writeAndFlush(new TextWebSocketFrame("[" + id + "]: " + msg));
                                  }

                                  @Override
                                  public void channelInactive(ChannelHandlerContext ctx) {
                                      CLIENTS.remove(ctx.channel());
                                      System.out.println("[聊天室] 有人下线,当前在线 " + CLIENTS.size() + " 人");
                                  }

                                  @Override
                                  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                                      // 心跳事件:读空闲太久,发个 ping 探活,或干脆踢掉死连接
                                      if (evt instanceof IdleStateEvent e && e.state() == IdleState.READER_IDLE) {
                                          System.out.println("[聊天室] 连接空闲超时,踢掉: " + shortId(ctx.channel()));
                                          ctx.close();
                                      }
                                  }

                                  @Override
                                  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                      ctx.close();
                                  }
                              });
                        }
                    })
                    .bind(Constants.PORT).sync();

            System.out.println("[聊天室] Netty WebSocket 服务启动: ws://127.0.0.1:" + Constants.PORT + "/ws");
            System.out.println("[聊天室] 打开 web/chat.html,多开几个标签页互发消息试试。\n");

        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }));
        }
    }

    /** 取 channel 短 id 当临时"用户名" */
    private static String shortId(Channel ch) {
        return ch.id().asShortText();
    }
}
