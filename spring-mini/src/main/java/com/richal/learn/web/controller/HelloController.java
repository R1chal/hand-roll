package com.richal.learn.web.controller;

import com.richal.learn.Component;
import com.richal.learn.web.*;

/**
 * 示例控制器：演示三种返回风格
 * - String：直接当作 HTML 文本返回
 * - @ResponseBody：对象转 JSON
 * - ModelAndView：返回视图名和上下文，交给极简模板引擎渲染
 *
 * Why：以最小示例覆盖 MVC 三种常见输出，便于自测与回归。
 */
@Controller
@RequestMapping("/hello")
@Component
public class HelloController {

    @RequestMapping("/a")
    public String a(@Param("name") String name, @Param("age") Integer age) {
        return String.format("<h1> hello world</h1><br> name: %s  age:%s", name, age);
    }

    @RequestMapping("/json")
    @ResponseBody
    public User json(@Param("name") String name, @Param("age") Integer age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        return user;
    }

    @RequestMapping("/html")
    public ModelAndView html(@Param("name") String name, @Param("age") Integer age) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("index.html");
        modelAndView.getContext().put("name", name);
        modelAndView.getContext().put("age", age.toString());
        return modelAndView;
    }

}
