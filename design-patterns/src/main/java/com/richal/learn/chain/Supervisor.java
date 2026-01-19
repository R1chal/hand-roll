package com.richal.learn.chain;

/**
 * 主管（组长）
 * 可以审批 1 天以内的请假
 *
 * @author Richal
 */
public class Supervisor extends Approver {

    public Supervisor(String name) {
        super(name);
    }

    @Override
    public String handleRequest(LeaveRequest request) {
        if (request.getLeaveDays() <= 1) {
            return String.format("[主管 %s] 批准了 %s 的请假申请（%d天）",
                name, request.getEmployeeName(), request.getLeaveDays());
        } else {
            if (nextApprover != null) {
                System.out.println(String.format("[主管 %s] 无权处理，转交给上级",
                    name));
                return nextApprover.handleRequest(request);
            } else {
                return String.format("[主管 %s] 无权处理，且没有上级审批者",
                    name);
            }
        }
    }
}
