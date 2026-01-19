package com.richal.learn.chain;

/**
 * 请假申请类
 * 表示一个请假请求
 *
 * @author Richal
 */
public class LeaveRequest {

    private String employeeName;
    private int leaveDays;
    private String reason;

    public LeaveRequest(String employeeName, int leaveDays, String reason) {
        this.employeeName = employeeName;
        this.leaveDays = leaveDays;
        this.reason = reason;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public int getLeaveDays() {
        return leaveDays;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return String.format("员工：%s，请假天数：%d天，原因：%s",
            employeeName, leaveDays, reason);
    }
}
