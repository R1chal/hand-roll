package com.richal.learn.web;

import java.util.HashMap;
import java.util.Map;

/**
 * 视图名 + 上下文数据 的简单载体。
 * 为什么不用复杂模板对象：教学/练习阶段关注“数据如何到页面”，
 * 只需记录视图文件名和一份 Map 即可。
 */
public class ModelAndView {

    private String view;

    private Map<String, Object> context = new HashMap<>();

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
