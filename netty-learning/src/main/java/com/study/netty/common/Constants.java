package com.study.netty.common;

/**
 * 全局常量:三个版本共用一个端口,方便你用同一个客户端分别去连。
 */
public class Constants {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8888;

    /** 手写原生 WebSocket 专用端口(和 Netty 版区分开,可以同时开) */
    public static final int RAW_WS_PORT = 8889;

    /** WebSocket 握手时的"魔法字符串",算 Sec-WebSocket-Accept 用,协议写死的 */
    public static final String WS_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private Constants() {}
}
