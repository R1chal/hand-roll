package com.richal.learn.web;

public class User {

    private String name;
    private Integer age;
    // 简单 Java Bean：用于 @ResponseBody JSON 返回的示例对象
    // Why：前后端联调时最常见的数据载体，字段保持最少即够用
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
