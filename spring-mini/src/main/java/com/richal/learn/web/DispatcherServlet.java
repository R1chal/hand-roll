package com.richal.learn.web;

import com.alibaba.fastjson2.JSONObject;
import com.richal.learn.BeanPostProcesser;
import com.richal.learn.Component;
import com.richal.learn.web.controller.Param;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 最小可用的“前端控制器”。
 * 这样设计的原因：把 路由、参数绑定、结果渲染 统一放到一个入口，
 * 学习和调试时能“一眼看懂请求是如何被处理的”。
 */
@Component
public class DispatcherServlet extends HttpServlet implements BeanPostProcesser {

    // 使用“请求URI -> 处理器”的 Map 做 O(1) 路由查找；
    // 不做通配/路径变量，保证演示时路由规则简单、可预期。
    Map<String, WebHandler> handlerMap = new HashMap<>();

    @Override
    /**
     * 统一处理入口：
     * 流程 = 路由匹配 → 参数绑定 → 反射调用 → 按返回类型渲染（HTML/JSON/本地视图）。
     *
     * 设计取舍：
     * - 不区分 doGet/doPost：学习阶段让流程更聚合，避免重复代码
     * - 异常统一转 ServletException：把反射细节对上层屏蔽
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebHandler controller = findController(req);
        if (Objects.isNull(controller)) {
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write("<h1>你的请求没有对应的处理器!</h1>");
        }
        try {
            Object controllerBean = controller.getControllerBean();
            Object[] args = resolveArgs(req, controller.getMethod());
            Object invoke = controller.getMethod().invoke(controllerBean, args);
            switch (controller.getResultType()) {
                case HTML -> {
                    resp.setContentType("text/html;charset=utf-8");
                    resp.getWriter().write(invoke.toString());
                }
                case JSON -> {
                    // 用注解模拟 Spring 的 @ResponseBody 语义，调用方零侵入
                    resp.setContentType("text/html;charset=utf-8");
                    resp.getWriter().write(JSONObject.toJSONString(invoke));
                }
                case LOCAL -> {
                    // 返回 ModelAndView 时，加载静态资源并做极简模板替换
                    // 注意：当前不做布局/条件/循环，仅做占位符替换，便于聚焦“数据如何到页面”
                    ModelAndView mv = (ModelAndView) invoke;
                    String view = mv.getView();
                    InputStream resource = this.getClass().getClassLoader().getResourceAsStream(view);
                    if (resource == null) {
                        resp.setStatus(404);
                        resp.getWriter().write("<h1>View not found: " + view + "</h1>");
                        break;
                    }
                    byte[] bytes = resource.readAllBytes();
                    String html = new String(bytes);
                    // 合并上下文与查询参数：控制器提供为主，URL 可补充/覆写缺失项
                    // 这样做的好处：
                    // - Controller 可给默认值
                    // - URL 参数可快速试验不同输入，便于调试
                    Map<String, Object> ctx = new HashMap<>();
                    if (mv.getContext() != null) {
                        ctx.putAll(mv.getContext());
                    }
                    req.getParameterMap().forEach((k, v) -> {
                        if (!ctx.containsKey(k) && v != null && v.length > 0) {
                            ctx.put(k, v[0]);
                        }
                    });
                    html = renderTemplate(html, ctx);
                    resp.setContentType("text/html;charset=utf-8");
                    resp.getWriter().write(html);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * 极简模板引擎：把 {{key}} / ${key} 替换成上下文中的字符串值。
     * 选择正则 + replaceAll 的原因：无三方依赖、实现短小，足够支撑学习演示。
     */
    private String renderTemplate(String html, Map<String, Object> ctx) {
        if (html == null) {
            return "";
        }
        if (ctx == null || ctx.isEmpty()) {
            return html;
        }

        String rendered = html;
        for (Map.Entry<String, Object> entry : ctx.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());

            // 支持两种占位符：{{ key }} 与 ${key}
            String mustachePattern = "\\{\\{\\s*" + Pattern.quote(key) + "\\s*\\}}";
            String springPattern = "\\$\\{\\s*" + Pattern.quote(key) + "\\s*\\}";

            rendered = rendered.replaceAll(mustachePattern, Matcher.quoteReplacement(value));
            rendered = rendered.replaceAll(springPattern, Matcher.quoteReplacement(value));
        }
        return rendered;
    }

    /**
     * 查询参数绑定：优先读取 @Param 指定的名称，
     * 若未标注则回退到参数本身的名称（编译加 -parameters 时生效）。
     */
    private Object[] resolveArgs(HttpServletRequest req, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String value= null;
            Param param = parameter.getAnnotation(Param.class);
            if (param!=null) {
                value = req.getParameter(param.value());
            }else {
                value = req.getParameter(parameter.getName());
            }
            Class<?> type = parameter.getType();
            if (String.class.isAssignableFrom(type)) {
                args[i] = value;
            }else if (Integer.class.isAssignableFrom(type)) {
                args[i] = Integer.parseInt(value);
            }
        }
        return args;
    }

    // 直接使用完整 URI 做键，避免路径变量/通配导致的不确定性
    private WebHandler findController(HttpServletRequest req) {
        WebHandler webHandler = handlerMap.get(req.getRequestURI());
        return webHandler;
    }

    @Override
    public Object afterInitializeBean(Object bean, String beanName) {
        // 放在 Bean 初始化后做路由收集：
        // 复用容器生命周期，不需要单独的“第二次扫描”。
        if (!bean.getClass().isAnnotationPresent(Controller.class)) {
            return bean;
        }
        RequestMapping classAnnotation = bean.getClass().getAnnotation(RequestMapping.class);
        String classUrl = classAnnotation != null ? classAnnotation.value() : "";
        Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(RequestMapping.class))
                .forEach(m -> {
                    RequestMapping methodAnnotation = m.getAnnotation(RequestMapping.class);
                    String key = classUrl.concat(methodAnnotation.value());
                    WebHandler webHandler = new WebHandler(bean, m);
                    if (handlerMap.put(key, webHandler) != null) {
                        throw new RuntimeException("定义重复");
                    }
                });
        return bean;
    }
}
