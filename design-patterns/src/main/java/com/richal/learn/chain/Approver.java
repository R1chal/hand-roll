package com.richal.learn.chain;

/**
 * 抽象处理器
 * 定义了处理请求的接口和责任链的结构
 *
 * @author Richal
 */
public abstract class Approver {

    protected String name;
    protected Approver nextApprover;

    public Approver(String name) {
        this.name = name;
    }

    /**
     * 设置下一个处理者
     *
     * @param nextApprover 下一个处理者
     */
    public void setNextApprover(Approver nextApprover) {
        this.nextApprover = nextApprover;
    }

    /**
     * 获取下一个处理者
     *
     * @return 下一个处理者
     */
    public Approver getNextApprover() {
        return nextApprover;
    }

    /**
     * 处理请假请求
     *
     * @param request 请假请求
     * @return 处理结果
     */
    public abstract String handleRequest(LeaveRequest request);

    /**
     * 获取处理者名称
     *
     * @return 处理者名称
     */
    public String getName() {
        return name;
    }
}
