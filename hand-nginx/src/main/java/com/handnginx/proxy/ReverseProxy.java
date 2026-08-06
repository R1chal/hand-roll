package com.handnginx.proxy;

import java.io.*;
import java.net.*;
import java.util.Map;

/**
 * 第5章：反向代理
 */
public class ReverseProxy {
    
    private String upstreamUrl;
    
    public ReverseProxy(String upstreamUrl) {
        this.upstreamUrl = upstreamUrl;
    }
    
    public byte[] forward(String method, String uri, Map<String, String> headers, byte[] body) {
        try {
            URL url = new URL(upstreamUrl + uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            if (body != null && body.length > 0) {
                conn.setDoOutput(true);
                try (OutputStream out = conn.getOutputStream()) {
                    out.write(body);
                }
            }
            
            int statusCode = conn.getResponseCode();
            String contentType = conn.getContentType();
            
            ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
            try (InputStream in = statusCode < 400 ? conn.getInputStream() : conn.getErrorStream()) {
                if (in != null) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        responseBody.write(buffer, 0, bytesRead);
                    }
                }
            }
            
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 ").append(statusCode).append(" ")
                   .append(conn.getResponseMessage()).append("\r\n");
            response.append("Content-Type: ").append(contentType != null ? contentType : "text/html").append("\r\n");
            response.append("Content-Length: ").append(responseBody.size()).append("\r\n");
            response.append("Server: hand-nginx/0.1.0\r\n");
            response.append("\r\n");
            
            byte[] headerBytes = response.toString().getBytes();
            byte[] bodyBytes = responseBody.toByteArray();
            
            byte[] result = new byte[headerBytes.length + bodyBytes.length];
            System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
            System.arraycopy(bodyBytes, 0, result, headerBytes.length, bodyBytes.length);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("[ReverseProxy] 转发失败: " + e.getMessage());
            return buildErrorResponse(502, "Bad Gateway", "后端服务不可用");
        }
    }
    
    private byte[] buildErrorResponse(int status, String statusText, String message) {
        String body = "<html><body><h1>" + status + " " + statusText + "</h1><p>" + message + "</p></body></html>";
        String response = "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                         "Content-Type: text/html\r\n" +
                         "Content-Length: " + body.length() + "\r\n" +
                         "\r\n" +
                         body;
        return response.getBytes();
    }
}
