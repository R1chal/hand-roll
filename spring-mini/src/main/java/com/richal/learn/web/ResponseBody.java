package com.richal.learn.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示方法返回体直接写回（JSON 文本）。
 * 为什么使用注解：最接近 Spring 语义，且对调用方零侵入、易理解。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
}
