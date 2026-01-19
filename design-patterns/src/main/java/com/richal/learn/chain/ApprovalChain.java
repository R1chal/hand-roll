package com.richal.learn.chain;

/**
 * 责任链构建器
 * 用于构建和管理审批责任链
 *
 * @author Richal
 */
public class ApprovalChain {

    private Approver firstApprover;

    /**
     * 构建标准的审批责任链
     * 主管 -> 经理 -> 总监 -> CEO
     *
     * @return 责任链的第一个处理者
     */
    public static ApprovalChain buildStandardChain() {
        ApprovalChain chain = new ApprovalChain();

        // 创建各级审批者
        Approver supervisor = new Supervisor("张三");
        Approver manager = new Manager("李四");
        Approver director = new Director("王五");
        Approver ceo = new CEO("赵六");

        // 构建责任链
        supervisor.setNextApprover(manager);
        manager.setNextApprover(director);
        director.setNextApprover(ceo);

        chain.firstApprover = supervisor;
        return chain;
    }

    /**
     * 自定义构建责任链
     *
     * @param approvers 审批者数组，按顺序连接
     * @return 责任链
     */
    public static ApprovalChain buildCustomChain(Approver... approvers) {
        ApprovalChain chain = new ApprovalChain();

        if (approvers == null || approvers.length == 0) {
            throw new IllegalArgumentException("审批者列表不能为空");
        }

        // 连接责任链
        for (int i = 0; i < approvers.length - 1; i++) {
            approvers[i].setNextApprover(approvers[i + 1]);
        }

        chain.firstApprover = approvers[0];
        return chain;
    }

    /**
     * 处理请假请求
     *
     * @param request 请假请求
     * @return 处理结果
     */
    public String process(LeaveRequest request) {
        if (firstApprover == null) {
            return "责任链未初始化";
        }

        System.out.println("\n========== 开始处理请假申请 ==========");
        System.out.println(request);
        System.out.println("--------------------------------------");

        String result = firstApprover.handleRequest(request);

        System.out.println("--------------------------------------");
        System.out.println("处理结果：" + result);
        System.out.println("========== 处理完成 ==========\n");

        return result;
    }

    /**
     * 获取责任链的第一个处理者
     *
     * @return 第一个处理者
     */
    public Approver getFirstApprover() {
        return firstApprover;
    }

    /**
     * 打印责任链结构
     */
    public void printChain() {
        System.out.println("========== 责任链结构 ==========");
        Approver current = firstApprover;
        int level = 1;

        while (current != null) {
            System.out.println(String.format("第%d级：%s (%s)",
                level, current.getName(), current.getClass().getSimpleName()));
            current = current.getNextApprover();
            level++;
        }
        System.out.println("================================\n");
    }
}
