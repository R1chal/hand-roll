package com.richal.learn.chain;

/**
 * 总监
 * 可以审批 7 天以内的请假
 *
 * @author Richal
 */
public class Director extends Approver {

    public Director(String name) {
        super(name);
    }

    @Override
    public String handleRequest(LeaveRequest request) {
        if (request.getLeaveDays() <= 7) {
            return String.format("[总监 %s] 批准了 %s 的请假申请（%d天）",
                name, request.getEmployeeName(), request.getLeaveDays());
        } else {
            if (nextApprover != null) {
                System.out.println(String.format("[总监 %s] 无权处理，转交给上级",
                    name));
                return nextApprover.handleRequest(request);
            } else {
                return String.format("[总监 %s] 无权处理，且没有上级审批者",
                    name);
            }
        }
    }
}
