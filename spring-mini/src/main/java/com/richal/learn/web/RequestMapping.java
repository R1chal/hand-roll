package com.richal.learn.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由映射：类/方法级别绑定 URL 片段。
 * 为什么只保留一个 value：保持最简 API，降低学习门槛，路由规则清晰可预测。
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();
}
