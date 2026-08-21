package com.study.netty.ws.raw;

import com.study.netty.common.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 【第五幕 · 前传】手写原生 WebSocket ——  Netty 替你扛的,这里你都得自己扛。
 *
 * 运行方式:mvn compile exec:java -Dexec.mainClass=com.study.netty.ws.raw.RawWebSocketServer
 *
 * 然后双击打开项目里的 web/chat.html,地址填 ws://127.0.0.1:8889 点连接。
 * 服务端会【回显】你发的每一条消息。
 *
 * 这个类故意把所有脏活摊开给你看,对照 Netty 版的 WsChatServer 数 ⚠️:
 *   HTTP Upgrade 握手 + SHA-1 + Base64   → Netty:WebSocketServerProtocolHandler 一行
 *   帧头按位解析(FIN/opcode/mask/变长长度) → Netty:WebSocketFrameDecoder 替你干
 *   掩码 unmask、分片帧重组、ping/pong    → Netty:全自动
 *
 * 看完这个类,你会发自内心地感谢 Netty。
 */
public class RawWebSocketServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Constants.RAW_WS_PORT, 1024);
        System.out.println("[RawWS] 手写原生 WebSocket 服务器启动,监听 " + Constants.RAW_WS_PORT);
        System.out.println("[RawWS] 用 web/chat.html 连接 ws://127.0.0.1:" + Constants.RAW_WS_PORT + " 体验回显。\n");

        while (true) {
            Socket socket = serverSocket.accept();
            // 教学版,沿用 BIO 模型:一个连接一个线程(反正重点在协议解析)
            new Thread(() -> handle(socket)).start();
        }
    }

    private static void handle(Socket socket) {
        try (socket;
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            // ============ 第一步:HTTP 握手(WebSocket 是从 HTTP "升级"来的) ============
            Map<String, String> headers = readHttpHeaders(in);
            String key = headers.get("sec-websocket-key");
            if (key == null) {
                System.out.println("[RawWS] 不是 WebSocket 握手请求,直接关闭");
                return;
            }
            // ⚠️ 天坑 1:握手应答 = Base64( SHA-1( key + 魔法字符串 ) ),算法是协议写死的。
            //    写错一个字符,浏览器就拒绝建立连接,而且只报一句"握手失败",让你debug到怀疑人生。
            String accept = sha1Base64(key + Constants.WS_MAGIC);
            String response = "HTTP/1.1 101 Switching Protocols\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Sec-WebSocket-Accept: " + accept + "\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();
            System.out.println("[RawWS] 握手成功,开始收发帧");

            // ============ 第二步:循环解析 WebSocket 帧(真正的体力活) ============
            StringBuilder fragmented = null; // 用来拼接被分片的消息
            while (true) {
                Frame frame = readFrame(in);
                if (frame == null) {
                    break; // 连接断开
                }
                switch (frame.opcode) {
                    case 0x8 -> { // Close 帧
                        System.out.println("[RawWS] 收到关闭帧,拜拜");
                        writeTextFrame(out, ""); // 礼貌地回一个 close
                        return;
                    }
                    case 0x9 -> // ⚠️ 天坑 2:必须回 Pong,否则客户端会判定连接已死、主动断开。
                            writeFrame(out, 0xA, frame.payload); // 0xA = Pong
                    case 0x1, 0x2, 0x0 -> { // 文本 / 二进制 / 续帧
                        // ⚠️ 天坑 3:分片。FIN=0 表示"话没说完,后面还有续帧"。
                        //    你得自己攒着,等 FIN=1 那条来了才拼成完整消息。这里简化处理。
                        String text = new String(frame.payload, StandardCharsets.UTF_8);
                        if (!frame.fin) {
                            fragmented = new StringBuilder(text);
                        } else if (fragmented != null) {
                            text = fragmented.append(text).toString();
                            fragmented = null;
                        }
                        if (frame.fin && fragmented == null) {
                            System.out.println("[RawWS] 收到文本 → [" + text + "]");
                            writeTextFrame(out, "原生回显: " + text);
                        }
                    }
                    default -> System.out.println("[RawWS] 忽略未知 opcode: " + frame.opcode);
                }
            }
        } catch (IOException e) {
            System.out.println("[RawWS] 连接断开: " + e.getMessage());
        }
    }

    /** 读 HTTP 请求头(逐行读到空行),把 header 名小写后塞进 map */
    private static Map<String, String> readHttpHeaders(InputStream in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = readLine(in)).isEmpty()) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                headers.put(line.substring(0, idx).trim().toLowerCase(),
                        line.substring(idx + 1).trim());
            }
        }
        return headers;
    }

    private static String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = in.read()) != -1 && c != '\n') {
            if (c != '\r') sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * ⚠️ 天坑重灾区:按 RFC6455 逐位解析一个帧。
     * 帧格式:
     *   第0字节: FIN(1位) + RSV(3位) + opcode(4位)
     *   第1字节: MASK(1位) + payload长度(7位,=126则后跟2字节长度,=127则后跟8字节)
     *   之后:   MASK=1 时有4字节掩码,再是(可能被异或加密的)真实数据
     */
    private static Frame readFrame(InputStream in) throws IOException {
        int b0 = in.read();
        if (b0 == -1) return null;
        int b1 = in.read();
        if (b1 == -1) return null;

        boolean fin = (b0 & 0x80) != 0;
        int opcode = b0 & 0x0F;
        boolean masked = (b1 & 0x80) != 0;
        long len = b1 & 0x7F;

        // 变长长度字段
        if (len == 126) len = readN(in, 2);
        else if (len == 127) len = readN(in, 8);

        // 客户端发来的帧【必须】带掩码(协议强制),服务端发的【必须】不带
        byte[] mask = masked ? readBytes(in, 4) : null;

        byte[] payload = readBytes(in, (int) len);
        // ⚠️ 天坑 4:unmask —— 数据 = 密文 ^ 掩码[i % 4],忘了这步读出来全是乱码
        if (mask != null) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= mask[i % 4];
            }
        }
        return new Frame(fin, opcode, payload);
    }

    private static long readN(InputStream in, int n) throws IOException {
        long v = 0;
        for (int i = 0; i < n; i++) {
            v = (v << 8) | (in.read() & 0xFF);
        }
        return v;
    }

    private static byte[] readBytes(InputStream in, int n) throws IOException {
        return in.readNBytes(n); // 教学简化:假定一次能读全(生产要处理"读了一半")
    }

    /** 写一个文本帧(服务端→客户端,不带掩码) */
    private static void writeTextFrame(OutputStream out, String text) throws IOException {
        writeFrame(out, 0x1, text.getBytes(StandardCharsets.UTF_8));
    }

    /** 组帧并写出:FIN=1 + opcode + 长度 + 数据 */
    private static void writeFrame(OutputStream out, int opcode, byte[] payload) throws IOException {
        out.write(0x80 | opcode); // FIN=1
        int len = payload.length;
        if (len < 126) {
            out.write(len);
        } else if (len < 65536) {
            out.write(126);
            out.write((len >>> 8) & 0xFF);
            out.write(len & 0xFF);
        } else {
            out.write(127);
            for (int i = 7; i >= 0; i--) out.write((int) ((len >>> (8 * i)) & 0xFF));
        }
        out.write(payload);
        out.flush();
    }

    private static String sha1Base64(String s) {
        try {
            return Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-1").digest(s.getBytes(StandardCharsets.ISO_8859_1)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** 一个解析好的帧 */
        private record Frame(boolean fin, int opcode, byte[] payload) {}
}
