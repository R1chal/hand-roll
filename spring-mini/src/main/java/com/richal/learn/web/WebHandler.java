package com.richal.learn.web;

import java.lang.reflect.Method;

/**
 * 控制器方法的运行时描述：保存实例、方法、以及渲染类型。
 * 为什么提前判定渲染类型：调用后无需再做复杂分支，降低运行时分支与耦合。
 *
 * 组成：
 * - controllerBean：控制器对象实例（被 IOC 管理）
 * - method：要调用的处理方法（反射对象）
 * - resultType：渲染类型（JSON/HTML/LOCAL），由注解与返回值类型推断
 *
 * 责任：
 * - 封装“怎么调用、调用后怎么渲染”的最小决策信息
 * - 对上层 DispatcherServlet 暴露极简接口，便于组合
 */
public class WebHandler {

    public Object getControllerBean() {
        return controllerBean;
    }

    public void setControllerBean(Object controllerBean) {
        this.controllerBean = controllerBean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public WebHandler(Object controllerBean, Method method) {
        this.controllerBean = controllerBean;
        this.method = method;
        this.resultType = resolveResultType(controllerBean, method);
    }

    private ResultType resolveResultType(Object controllerBean, Method method) {

        if(method.isAnnotationPresent(ResponseBody.class)) {
            return ResultType.JSON;
        }
        if(method.getReturnType() == ModelAndView.class) {
            return ResultType.LOCAL;
        }
        return ResultType.HTML;
    }

    private Object controllerBean;

    private Method method;

    private ResultType resultType;

    public ResultType getResultType() {
        return resultType;
    }

    enum ResultType{
        JSON,HTML,LOCAL
    }

}
