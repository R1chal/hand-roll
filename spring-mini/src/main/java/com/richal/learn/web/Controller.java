package com.richal.learn.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个类为“控制器”。
 * 为什么单独定义：与核心的 @Component 解耦，便于 MVC 模块单独识别、扩展扫描规则。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
