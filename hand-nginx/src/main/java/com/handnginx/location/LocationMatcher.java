package com.handnginx.location;

import com.handnginx.config.NginxConfig.LocationConfig;
import java.util.*;

/**
 * 第4章：location 为什么能匹配 URL
 * 
 * Nginx location 匹配设计思想：
 * 1. 最长前缀匹配 - 选择最精确的匹配
 * 2. 优先级顺序 - = > ^~ > ~* > ~ > 无修饰符
 * 3. 正则匹配 - 支持正则表达式
 * 4. 匹配后停止 - 找到匹配后不再继续
 * 
 * 匹配优先级（从高到低）：
 * 1. =           精确匹配
 * 2. ^~          前缀匹配（匹配后停止正则）
 * 3. ~           正则匹配（区分大小写）
 * 4. ~*          正则匹配（不区分大小写）
 * 5. 无修饰符     前缀匹配
 * 
 * 手写版简化：
 * - 支持前缀匹配
 * - 支持精确匹配
 * - 简化正则匹配
 */
public class LocationMatcher {
    
    private Map<String, LocationConfig> locations;
    
    public LocationMatcher(Map<String, LocationConfig> locations) {
        this.locations = locations;
    }
    
    /**
     * 匹配 URL
     * 
     * Nginx 真实实现：
     * - 先进行前缀匹配，记录最长匹配
     * - 如果有 ^~ 修饰符，停止正则匹配
     * - 否则进行正则匹配
     * - 正则匹配成功则使用正则 location
     * - 否则使用最长前缀匹配
     * 
     * 手写版简化：
     * - 只实现前缀匹配
     * - 选择最长匹配
     */
    public MatchResult match(String uri) {
        String bestMatch = null;
        LocationConfig bestConfig = null;
        
        for (Map.Entry<String, LocationConfig> entry : locations.entrySet()) {
            String pattern = entry.getKey();
            
            // 精确匹配
            if (pattern.equals(uri)) {
                return new MatchResult(pattern, entry.getValue(), MatchType.EXACT);
            }
            
            // 前缀匹配：选择最长的匹配
            // Nginx 核心：location /api 能匹配 /api/user
            if (uri.startsWith(pattern)) {
                if (bestMatch == null || pattern.length() > bestMatch.length()) {
                    bestMatch = pattern;
                    bestConfig = entry.getValue();
                }
            }
        }
        
        if (bestMatch != null) {
            return new MatchResult(bestMatch, bestConfig, MatchType.PREFIX);
        }
        
        return null;
    }
    
    /**
     * 匹配结果
     */
    public static class MatchResult {
        private final String pattern;
        private final LocationConfig config;
        private final MatchType type;
        
        public MatchResult(String pattern, LocationConfig config, MatchType type) {
            this.pattern = pattern;
            this.config = config;
            this.type = type;
        }
        
        public String getPattern() { return pattern; }
        public LocationConfig getConfig() { return config; }
        public MatchType getType() { return type; }
    }
    
    public enum MatchType {
        EXACT,   // 精确匹配
        PREFIX   // 前缀匹配
    }
}
