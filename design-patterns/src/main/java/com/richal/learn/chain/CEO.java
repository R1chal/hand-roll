package com.richal.learn.chain;

/**
 * CEO（总经理）
 * 可以审批任意天数的请假
 *
 * @author Richal
 */
public class CEO extends Approver {

    public CEO(String name) {
        super(name);
    }

    @Override
    public String handleRequest(LeaveRequest request) {
        // CEO 可以处理任意天数的请假
        if (request.getLeaveDays() > 30) {
            return String.format("[CEO %s] 拒绝了 %s 的请假申请（%d天），请假天数过长",
                name, request.getEmployeeName(), request.getLeaveDays());
        } else {
            return String.format("[CEO %s] 批准了 %s 的请假申请（%d天）",
                name, request.getEmployeeName(), request.getLeaveDays());
        }
    }
}
